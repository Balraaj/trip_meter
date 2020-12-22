package com.forall.tripmeter.repository

import android.location.Location
import com.forall.tripmeter.database.Database
import com.forall.tripmeter.prefs.TripMeterSharedPrefs

class ServiceRepository(private val db: Database,
                        private val prefs: TripMeterSharedPrefs) {

    fun isTripActive(): Boolean = prefs.isTripActive
    fun setTripActive(state: Boolean) { prefs.isTripActive = state }

    fun updateLocation(location: Location){
        db.locationDao().updateLocation(location.latitude,
            location.longitude, location.speed.toInt())
    }

    fun updateCurrentAddress(address: String) = db.locationDao().updateAddress(address)

    fun removeLastKnownLocation() = db.locationDao().deleteLocation()
}