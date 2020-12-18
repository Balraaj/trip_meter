package com.forall.tripmeter.service

import android.annotation.SuppressLint
import android.app.*
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.forall.tripmeter.common.AppComponentProvider
import com.forall.tripmeter.di.component.DaggerServiceComponent
import com.forall.tripmeter.repository.ServiceRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class LocationService : Service() {

    private val binder: IBinder = ServiceBinder()

    private lateinit var repo: ServiceRepository
    private lateinit var geocoder: Geocoder
    private lateinit var locationRequest: LocationRequest
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationClient: FusedLocationProviderClient

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.first()
                onNewLocation(location)
            }
        }
    }

    override fun onCreate() {
        injectDependencies()
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        createLocationRequest()
        setupNotificationManager()
    }

    private fun injectDependencies(){
        val serviceComponent = DaggerServiceComponent.factory()
            .create((application as AppComponentProvider).getAppComponent())
        repo = serviceComponent.getServiceRepository()
    }

    /**
     * Builds the location request by configuring the location request interval
     * and the accuracy of location request.
     * @author Balraj
     */
    private fun createLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int) = START_NOT_STICKY

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
            startForeground(NOTIFICATION_ID, getNotification(0))
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
        repo.removeLastKnownLocation()
        removeLocationUpdates()
        stopSelf()
    }

    /**
     * Starts the location updates using FusedLocationProvider.
     * @author Balraj
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        startService(Intent(applicationContext, LocationService::class.java))
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    /**
     * Removes the location updates from fusedLocationProvider.
     * @author Balraj
     */
    private fun removeLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Returns the notification to be displayed when service is moved
     * to foreground.
     * @return notificaiton
     * @author Balraj
     */
    private fun getNotification(speed: Int): Notification {
        val text: CharSequence = "$speed KMPH"
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Current Speed")
            .setContentText(text)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(com.forall.tripmeter.R.mipmap.ic_launcher)
            .setTicker(text)
            .setOnlyAlertOnce(true)
            .setWhen(System.currentTimeMillis())
        return builder.build()
    }

    private fun onNewLocation(location: Location) {
        if (serviceIsRunningInForeground()) {
            notificationManager.notify(NOTIFICATION_ID, getNotification(location.speed.toInt()))
        }
        setLocationAddress(location)
        storeLocationInRoom(location)
    }

    private fun storeLocationInRoom(location: Location) = GlobalScope.launch(Dispatchers.IO) {
        repo.updateLocation(location)
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class ServiceBinder : Binder() {
        fun getService() = this@LocationService
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

    /**
     * Tries to determine the address of given location.
     * if address is found then updates the location table
     * @author Balraj
     */
    private fun setLocationAddress(loc: Location){
        Thread{
            try {
            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
            if (addresses.isNotEmpty() && addresses[0].maxAddressLineIndex != -1) {
                val address = addresses[0].getAddressLine(0)
                repo.updateCurrentAddress(address)
            }
        }
        catch (e: IOException) {
            Log.d(TAG, "GEO_CODER: FAILED TO GET LOCATION ADDRESS")
        }}.start()
    }

    companion object {
        private val TAG = LocationService::class.java.simpleName
        private const val CHANNEL_ID = "tripMeter_channel_01"
        private const val UPDATE_INTERVAL = 1000L
        private const val FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2
        private const val NOTIFICATION_ID = 12345678
    }
}