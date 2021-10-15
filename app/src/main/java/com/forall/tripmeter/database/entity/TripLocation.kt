package com.forall.tripmeter.database.entity

import android.location.Location
import android.system.Os
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.forall.tripmeter.common.Constants.EMPTY_STRING

/**
 * Project Name : Trip Meter
 * @author Balraj
 * @date   Dec 16, 2020
 *
 * Description
 * -----------------------------------------------------------------------------------
 * TripLocation : Encapsulates the location data captured by the service.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@Entity(tableName = "trip_location")
class TripLocation(
    @PrimaryKey(autoGenerate = true)
    val locationId: Int,
    var address: String,
    var speed: Int,
    var lat: Double,
    var lon: Double,
    val timestamp: Long
){
    fun toAndroidLocation() = Location(LATEST_LOCATION).apply { latitude = lat; longitude = lon }

    /**
     * Creates a copy of this instance and updates the speed, lat and lon
     * from android location. this is used whenever a new location is reported by hardware.
     */
    fun updateWithAndroidLocation(location: Location) = TripLocation(
        locationId,
        address,
        location.speed.toInt(),
        location.latitude,
        location.longitude,
        System.currentTimeMillis()
    )

    companion object {
        private const val LATEST_LOCATION = "LATEST_LOCATION"
        private const val DEFAULT_LOCATION_ID = 1
        private const val DEFAULT_LOCATION_ADDRESS = EMPTY_STRING

        /**
         * Creates a default instance from the given android location instance.
         */
        fun fromAndroidLocation(androidLocation: Location) = TripLocation(
            DEFAULT_LOCATION_ID,
            DEFAULT_LOCATION_ADDRESS,
            androidLocation.speed.toInt(),
            androidLocation.latitude,
            androidLocation.longitude,
            System.currentTimeMillis()
        )
    }
}