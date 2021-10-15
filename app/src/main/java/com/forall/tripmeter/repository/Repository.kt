package com.forall.tripmeter.repository

import com.forall.tripmeter.database.Database
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.database.entity.TripLocation
import com.forall.tripmeter.prefs.TripMeterSharedPrefs

class Repository(private val db: Database,
                 private val prefs: TripMeterSharedPrefs) {

    fun isMeasurementUnitMiles() = prefs.isMeasurementUnitMiles()

    fun isTripActive() = prefs.isTripActive

    fun isTripActive(value: Boolean) { prefs.isTripActive = value }

    suspend fun insertTrip(trip: Trip) = db.tripDao().insertTrip(trip)

    fun deleteLatestTrip() = db.tripDao().deleteLatestTrip()

    fun getAllTrips() = db.tripDao().getAllTrips()

    fun getLatestTripLive() = db.tripDao().getLatestTripLive()

    suspend fun getLatestTrip() = db.tripDao().getLatestTrip()

    fun getLastKnownLocation() = db.locationDao().getLastKnownLocation()
}