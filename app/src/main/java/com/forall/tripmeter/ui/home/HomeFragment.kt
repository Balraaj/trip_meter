package com.forall.tripmeter.ui.home

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.di.component.FragmentComponent
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment<HomeViewModel>() {

    private lateinit var locationRequest: LocationRequest
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun provideLayoutId() = R.layout.fragment_home
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)

    override fun setupView(view: View) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {
            it?.let { showMessage(it) }
        })
    }

    override fun onResume() {
        super.onResume()
        locationRequest = LocationRequest()
        locationRequest.interval = 1000 // two minute interval
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
        val locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
    }


    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.first()
                Log.i("MapsActivity", "Speed: " + (location.speed)*3.6)
                tv_speed.text = ((location.speed) * 3.6).toInt().toString()
                lastLocation = location
            }
        }
    }

    private val locationListener = LocationListener { location ->
        Log.i("MapsActivity", "Speed: LocationManager" + (location.speed)*3.6)
//        tv_speed.text =  (location.speed * 3.6).toInt().toString()
    }
}