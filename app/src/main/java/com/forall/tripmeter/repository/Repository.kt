package com.forall.tripmeter.repository

import com.forall.tripmeter.database.Database
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.prefs.TripMeterSharedPrefs

class Repository(private val db: Database,
                 private val prefs: TripMeterSharedPrefs) {

    fun insertTrip(trip: Trip) = db.tripDao().insertTrip(trip)
    fun deleteLatestTrip() = db.tripDao().deleteLatestTrip()

    fun updateCurrentTrip(t: Trip) {
        db.tripDao().updateTrip(t.tripId, t.endAddress, t.endTime,
            t.endLat, t.endLong, t.distance, t.speed)
    }

    fun getAllTrips() = db.tripDao().getAllTrips()

    fun getLatestTrip() = db.tripDao().getLatestTrip()
}