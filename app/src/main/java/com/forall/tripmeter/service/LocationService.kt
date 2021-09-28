package com.forall.tripmeter.service

import android.annotation.SuppressLint
import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.forall.tripmeter.R
import com.forall.tripmeter.common.AppComponentProvider
import com.forall.tripmeter.common.inKmph
import com.forall.tripmeter.common.inMiles
import com.forall.tripmeter.di.component.DaggerServiceComponent
import com.forall.tripmeter.repository.ServiceRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class LocationService : Service() {

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class ServiceBinder : Binder() {
        fun getService() = this@LocationService
    }

    companion object {
        private val TAG = LocationService::class.java.simpleName
        private const val CHANNEL_ID = "tripMeter_channel_01"
        private const val UPDATE_INTERVAL = 2000L
        private const val NOTIFICATION_ID = 12345678

        private const val SPEED_CAP = 55F
        private const val MAX_SPEED_JUMP = 10
        private const val LOCATION_MIN_DISTANCE = 5F
        private const val LOCATION_MIN_INTERVAL = 1000L
    }

    private val binder: IBinder = ServiceBinder()

    private lateinit var repo: ServiceRepository
    private lateinit var geocoder: Geocoder
    private lateinit var locationRequest: LocationRequest
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var lastKnownLocation: Location

    /** Use LocationManager instead of Fused for accurate location results. */
    private val locationListener = LocationListener { onNewLocation(it) }

    /** Configure Fused, to get GPS locks fast */
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {}
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int) = START_STICKY

    override fun onBind(intent: Intent): IBinder? {
        stopForeground(true)
        return binder
    }

    override fun onRebind(intent: Intent) {
        stopForeground(true)
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        if (repo.isTripActive()) {
            startForeground(NOTIFICATION_ID, getNotification("0"))
        }
        else { stopSelf() }
        return true
    }

    /**
     * When app is killed, remove location updates and stop the service
     * @author Balraj
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        repo.setTripActive(false)
        repo.removeLastKnownLocation()
        removeLocationUpdates()
        stopSelf()
    }

    /**
     * Starts the location updates using FusedLocationProvider and LocationManager.
     * @author Balraj
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        startService(Intent(applicationContext, LocationService::class.java))
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_MIN_INTERVAL,
            LOCATION_MIN_DISTANCE,
            locationListener
        )
    }

    /**
     * Removes the location updates from FusedLocationProvider and LocationManager.
     * @author Balraj
     */
    private fun removeLocationUpdates() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(locationListener)
        locationClient.removeLocationUpdates(locationCallback)
    }

    override fun onCreate() {
        injectDependencies()
        initLocationProperties()
        setupNotificationManager()
    }

    private fun injectDependencies(){
        val serviceComponent = DaggerServiceComponent.factory()
            .create((application as AppComponentProvider).getAppComponent())
        repo = serviceComponent.getServiceRepository()
    }

    /**
     * Initializes all the properties required for location capturing.
     * @author Balraj
     */
    private fun initLocationProperties() {
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    /**
     * Sets up the notification manager for posting the foreground notification.
     * will also setup notification channel if used on OREO or later
     * @author Balraj
     */
    private fun setupNotificationManager(){
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        /* Android O requires a Notification Channel. */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(com.forall.tripmeter.R.string.app_name)
            val channel = NotificationChannel(CHANNEL_ID, name, IMPORTANCE_HIGH)
            channel.setSound(null, null)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Returns the notification to be displayed when service is moved
     * to foreground.
     * @return notificaiton
     * @author Balraj
     */
    private fun getNotification(text: String): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Current Speed")
            .setContentText(text)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker(text)
            .setOnlyAlertOnce(true)
            .setWhen(System.currentTimeMillis())
        return builder.build()
    }

    /**
     * Returns true if this is a foreground service.
     * @return true if this is a foreground service false otherwise.
     */
    @Suppress("DEPRECATION")
    private fun serviceIsRunningInForeground(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                return service.foreground
            }
        }
        return false
    }

    private fun onNewLocation(location: Location) {
        if (serviceIsRunningInForeground()) {
            val notificationText = if(repo.isMeasurementUnitMiles()) {
                "${location.speed.inMiles()} MPH"
            } else { "${location.speed.inKmph()} KMPH" }
            notificationManager.notify(NOTIFICATION_ID, getNotification(notificationText))
        }
        setLocationAddress(location)
        storeLocationInRoom(location)
    }

    /**
     * Tries to determine the address of given location.
     * if address is found then updates the location table
     * @author Balraj
     */
    private fun setLocationAddress(loc: Location) = GlobalScope.launch(Dispatchers.IO) {
        try {
            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
            if (addresses.isNotEmpty() && addresses.first().maxAddressLineIndex != -1) {
                val address = addresses.first().getAddressLine(0)
                repo.updateCurrentAddress(address)
            }
        }
        catch (e: IOException) { Log.e(TAG, "GEO_CODER: FAILED TO GET LOCATION ADDRESS") }
    }

    /**
     * Stores the captured location in local database.
     * only allows a limited speed jump, automatically adjusts speed if required.
     * @author Balraj
     */
    private fun storeLocationInRoom(location: Location) = GlobalScope.launch(Dispatchers.IO) {
        if(::lastKnownLocation.isInitialized){
            if(location.speed - lastKnownLocation.speed > MAX_SPEED_JUMP){
                location.speed = lastKnownLocation.speed + MAX_SPEED_JUMP
            }
            if(location.speed > SPEED_CAP) { location.speed = SPEED_CAP }
        }
        repo.updateLocation(location)
        lastKnownLocation = location
    }
}