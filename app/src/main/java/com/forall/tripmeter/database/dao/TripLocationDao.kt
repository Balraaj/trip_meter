package com.forall.tripmeter.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.forall.tripmeter.database.entity.TripLocation

@Dao
interface TripLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripLocation(location: TripLocation)

    @Query("UPDATE trip_location SET address =:address")
    suspend fun updateAddress(address: String)

    @Query("SELECT * FROM trip_location")
    fun getLastKnownLocation(): LiveData<TripLocation>

    @Query("DELETE FROM trip_location")
    fun deleteLocation()

    @Query("SELECT * FROM trip_location LIMIT 1")
    fun getLastLocation(): TripLocation?
}