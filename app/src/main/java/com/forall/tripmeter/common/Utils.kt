package com.forall.tripmeter.common

import java.math.BigDecimal
import java.text.SimpleDateFormat

object Utils {

    private val TRIP_TIME_FORMATTER = SimpleDateFormat("d MMM yyyy hh:mm a")

    fun millisToTripTimeFormat(millis: Long): String{
        return TRIP_TIME_FORMATTER.format(millis)
    }

    fun metersToKM(meters: Float): Float{
        return BigDecimal(meters / 1000.0).setScale(1, BigDecimal.ROUND_DOWN).toFloat()
    }

    fun metersToMiles(meters: Float): Float{
        return BigDecimal(meters / 1609.0).setScale(1, BigDecimal.ROUND_DOWN).toFloat()
    }

    fun kmphToMph(kmph: Int): Int{
        return (kmph / 1.609).toInt()
    }
}

fun Float.inKmph(): Int = (this * Constants.FACTOR_METER_TO_KM).toInt()

fun Float.inMiles(): Int = (this * Constants.FACTOR_METER_TO_MILES).toInt()