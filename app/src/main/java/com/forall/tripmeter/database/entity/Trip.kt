package com.forall.tripmeter.database.entity

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.forall.tripmeter.common.Constants
import java.util.concurrent.TimeUnit

/**
 * Project Name : Trip Meter
 * @author Balraj
 * @date   Nov 12, 2020
 *
 * Description
 * -----------------------------------------------------------------------------------
 * Trip : Class for are most important entity.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val tripId: Int,
    val startAddress: String,
    val endAddress: String,
    val startTime: Long,
    val endTime: Long,
    val distance: Float,
    val speed: Int,
    val startLat: Double,
    val startLong: Double,
    val endLat: Double,
    val endLong: Double
){
    fun updateWithNewLocation(newLocation: TripLocation): Trip{
        val(speed, distance) = getSpeedAndDistanceWithNewLocation(newLocation)
        return Trip(
            tripId       = tripId,
            startAddress = startAddress,
            endAddress   = newLocation.address,
            startTime    = startTime,
            endTime      = newLocation.timestamp,
            distance     = distance,
            speed        = speed,
            startLat     = startLat,
            startLong    = startLong,
            endLat       = newLocation.lat,
            endLong      = newLocation.lon
        )
    }

    private fun getSpeedAndDistanceWithNewLocation(newLocation: TripLocation): Pair<Int, Float>{
        val currentLocation = newLocation.toAndroidLocation()
        val tripLastLocation = Location(Constants.LAST_LOCATION).apply {
            latitude  = endLat
            longitude = endLong
        }
        val distance = currentLocation.distanceTo(tripLastLocation) + this.distance
        var speed = when {
            (distance > 200) -> (distance / (TimeUnit.MILLISECONDS.toSeconds(newLocation.timestamp - this.startTime))).toInt()
            else             -> Constants.ZERO
        }
        return Pair(speed, distance)
    }

    companion object{

        /** Creates a new trip from the given trip location. */
        fun fromLocation(location: TripLocation) = Trip(
            tripId = 0,
            startAddress = location.address,
            endAddress   = location.address,
            startTime    = System.currentTimeMillis(),
            endTime      = System.currentTimeMillis(),
            distance     = 0.0f,
            speed        = 0,
            startLat     = location.lat,
            startLong    = location.lon,
            endLat       = location.lat,
            endLong      = location.lon
        )
    }
}