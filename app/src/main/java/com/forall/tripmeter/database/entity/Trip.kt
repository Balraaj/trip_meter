package com.forall.tripmeter.database.entity

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.function.DoubleBinaryOperator

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
class Trip(
    @PrimaryKey(autoGenerate = true)
    val tripId: Int,

    var startAddress: String,
    var endAddress: String,
    var startTime: Long,
    var endTime: Long,
    var distance: Float,
    var speed: Int,
    var startLat: Double,
    var startLong: Double,
    var endLat: Double,
    var endLong: Double
){

    fun updateWithDelta(distance: Float, speed: Int, newDelta: Trip){
        this.endAddress = newDelta.endAddress
        this.endTime = newDelta.endTime
        this.distance = distance
        this.speed = speed
        this.endLat = newDelta.endLat
        this.endLong = newDelta.endLong
    }

    companion object {

        fun defaultDelta(location: Location, address: String): Trip{
            return Trip(0, address, address,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                0.0f, 0, location.latitude, location.longitude, location.latitude, location.longitude)
        }

    }
}