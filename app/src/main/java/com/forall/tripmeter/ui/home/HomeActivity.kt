package com.forall.tripmeter.ui.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.ServiceConnection
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseActivity
import com.forall.tripmeter.di.component.ActivityComponent
import com.forall.tripmeter.service.LocationService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.content_main.*


class HomeActivity : BaseActivity<HomeViewModel>() {

    private companion object {
        private const val PACKAGE = "package"
        private const val PERMISSION_REQUEST = 101
        private const val LOCATION_INTERVAL = 1000L
        private const val LOCATION_SETTINGS_REQUEST = 1001
    }

    private var locationService: LocationService? = null
    private lateinit var navController: NavController
    private lateinit var locationRequest: LocationRequest

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.ServiceBinder
            locationService = binder.getService()
            if(viewModel.permissionAllowed) { startLocationUpdates() }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
        }
    }

    override fun provideLayoutId() = R.layout.content_main
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        navController = findNavController(R.id.nav_host_fragment)
        nav_bar_bottom.setupWithNavController(navController)
        setupNavLocationChangeListener()
        createLocationRequest()
        checkPermissionsAndContinue()
    }

    /**
     * When activity starts bind the location service in order to receive
     * the location updates.
     * @author Balraj
     */
    override fun onStart() {
        super.onStart()
        viewModel.permissionAllowed = false
        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * When activity stops unbind the location service,
     * which signals the service to promote itself to foreground service.
     * @author Balraj
     */
    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    private fun setupNavLocationChangeListener(){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
        }
    }

    /**
     * Builds the location request by configuring the location request interval
     * and the accuracy of location request.
     * @author Balraj
     */
    private fun createLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    
    /**
     * Before starting location updates we need to check if all the required permissions
     * are granted, if not then notify the user with appropriate message.
     * @author Balraj
     */
    private fun checkPermissionsAndContinue(){
        Dexter.withContext(this)
            .withPermissions(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report == null) { return }
                    if(report.areAllPermissionsGranted()) { checkGPSStatus()}
                    else {
                        val message = getString(R.string.permissions_required)
                        showConfirmationDialog(message, onSettingsDialogConfirmed, onSettingsDialogCanceled)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun checkGPSStatus(){
        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        settingsBuilder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(this)
            .checkLocationSettings(settingsBuilder.build())
        result.addOnCompleteListener(listener)
    }

    /**
     * Starts location updates from location service and LocationManager for better accuracy.
     * we do this for accuracy.
     * @author Balraj
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationService?.requestLocationUpdates()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(GPS_PROVIDER, LOCATION_INTERVAL, 5f, locationListener)
    }

    /**
     * When user cancels settings dialog, hence not allowing permissions.
     * notify the user of inability to work and kill the app.
     * @author Balraj
     */
    private val onSettingsDialogCanceled: () -> Unit = {
        val message = getString(R.string.permissions_denied)
        showNotificationDialog(message){ finish() }
    }

    /**
     * When user confirms settings dialog then move to the app settings page.
     * result of this action is handled in onActivityResult.
     * @author Balraj
     */
    private val onSettingsDialogConfirmed: () -> Unit = {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts(PACKAGE, packageName, null)
        }
        startActivityForResult(intent, PERMISSION_REQUEST)
    }

    /**
     * After visiting settings check if user has granted the required
     * permissions, if not then show the permissions required dialog
     * @author VE00YM023
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST) { checkPermissionsAndContinue() }
        else if(requestCode == LOCATION_SETTINGS_REQUEST){
            if(resultCode == Activity.RESULT_OK) {
                viewModel.permissionAllowed = true
                startLocationUpdates()
            }
            else {
                var message = getString(R.string.enable_gps)
                showConfirmationDialog(message, { checkGPSStatus() }){
                    message = getString(R.string.location_required)
                    showNotificationDialog(message) { finish() }
                }
            }
        }
    }

    /**
     * This is here just to improve accuracy of fused provider.
     * may be in future we can make user of it.
     * @author Balraj
     */
    private val locationListener = object: LocationListener {
        override fun onLocationChanged(location: Location) {}

        override fun onProviderDisabled(provider: String) {
            if (provider == GPS_PROVIDER) checkGPSStatus()
        }

        override fun onProviderEnabled(provider: String) {
            checkGPSStatus()
        }
    }

    private val listener = OnCompleteListener<LocationSettingsResponse> { task ->
        try {
            val response = task.getResult(ApiException::class.java)
            if(response.locationSettingsStates.isGpsPresent &&
               response.locationSettingsStates.isGpsUsable) {
                viewModel.permissionAllowed = true
                startLocationUpdates()
            }
            else { throw ApiException(Status(6)) }
        }
        catch (ex: ApiException) {
            if (ex.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                try {
                    val e = ex as ResolvableApiException
                    e.startResolutionForResult(this, LOCATION_SETTINGS_REQUEST)
                }
                catch (e: SendIntentException) { }
            }
        }
    }
}