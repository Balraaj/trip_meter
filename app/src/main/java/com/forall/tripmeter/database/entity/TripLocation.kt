package com.forall.tripmeter.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    var lon: Double
){
    fun isNotDefaultLocation() = this.lat != 0.0 && this.lon != 0.0

    companion object {
        fun getDefaultLocation() = TripLocation(1, "Not Available", 0, 0.0, 0.0)
    }
}