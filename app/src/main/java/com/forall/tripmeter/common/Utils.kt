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
}

fun Float.inKmph(): Int = (this * 3.6).toInt()