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
    fun insertTripLocation(location: TripLocation)

    @Query("UPDATE trip_location SET lat =:lat, lon =:lon, speed =:speed")
    fun updateLocation(lat: Double, lon: Double, speed: Int)

    @Query("UPDATE trip_location SET address =:address")
    fun updateAddress(address: String)

    @Query("SELECT * FROM trip_location LIMIT 1")
    fun getLastKnownLocation(): LiveData<TripLocation>

    @Query("DELETE FROM trip_location")
    fun deleteLocation()

    @Query("SELECT * FROM trip_location LIMIT 1")
    fun getLastKnownLocationNotLive(): TripLocation
}