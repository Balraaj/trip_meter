package com.forall.tripmeter.repository

import android.location.Location
import com.forall.tripmeter.database.Database
import com.forall.tripmeter.database.entity.TripLocation
import com.forall.tripmeter.prefs.TripMeterSharedPrefs

class ServiceRepository(private val db: Database,
                        private val prefs: TripMeterSharedPrefs) {

    fun isMeasurementUnitMiles() = prefs.isMeasurementUnitMiles()

    fun isTripActive(): Boolean = prefs.isTripActive
    fun setTripActive(state: Boolean) { prefs.isTripActive = state }

    suspend fun updateLocation(location: Location){
        val newTripLocation = when(val oldTripLocation = db.locationDao().getLastLocation()){
            null -> { TripLocation.fromAndroidLocation(location) }
            else -> { oldTripLocation.updateWithAndroidLocation(location) }
        }
        db.locationDao().insertTripLocation(newTripLocation)
    }

    suspend fun updateCurrentAddress(address: String) = db.locationDao().updateAddress(address)

    fun removeLastKnownLocation() = db.locationDao().deleteLocation()


    /**
     * Updates the currently active trip with the new location information.
     * Following fields of current trip are updated:
     *      1. distance (add new distance)
     *      2. speed (calculate new average)
     *      3. endLat
     *      4. endLong
     *      5. endTime
     *      6. endAddress
     *
     * @author Balraj
     */
    suspend fun updateTrip(){
        val latestTrip     = db.tripDao().getLatestTrip()       ?: return
        val latestLocation = db.locationDao().getLastLocation() ?: return
        val updatedTrip = latestTrip.updateWithNewLocation(latestLocation)
        db.tripDao().updateTrip(updatedTrip)
    }
}

