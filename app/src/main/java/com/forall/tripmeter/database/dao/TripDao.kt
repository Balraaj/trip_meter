package com.forall.tripmeter.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.forall.tripmeter.database.entity.Trip

@Dao
interface TripDao {

    @Insert
    suspend fun insertTrip(trip: Trip)

    @Update
    fun updateTrip(trip: Trip)

    @Query("SELECT * FROM trip ORDER BY tripId DESC")
    fun getAllTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trip WHERE tripId = (SELECT MAX(tripId) FROM trip)")
    suspend fun getLatestTrip(): Trip?

    @Query("SELECT * FROM trip WHERE tripId = (SELECT MAX(tripId) FROM trip)")
    fun getLatestTripLive(): LiveData<Trip>

    @Query("DELETE FROM trip WHERE tripId = (SELECT MAX(tripId) FROM trip)")
    fun deleteLatestTrip(): Int

    @Query("SELECT COUNT(*) FROM trip")
    fun getTripCount(): Int
}