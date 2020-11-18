package com.forall.tripmeter.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.forall.tripmeter.database.entity.Trip

@Dao
interface TripDao {

    @Insert
    fun insertTrip(trip: Trip)

    @Query("UPDATE trip SET endAddress =:endAddress, endTime=:endTime, endLat =:endLat, endLong =:endLong, distance =:distance, speed =:speed WHERE tripId =:tripId ")
    fun updateTrip(tripId: Int, endAddress: String, endTime: Long, endLat: Double, endLong: Double, distance: Float, speed: Int)

    @Query("SELECT * FROM trip ORDER BY tripId DESC")
    fun getAllTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trip WHERE tripId = (SELECT MAX(tripId) FROM trip)")
    fun getLatestTrip(): Trip?

    @Query("DELETE FROM trip WHERE tripId = (SELECT MAX(tripId) FROM trip)")
    fun deleteLatestTrip(): Int
}