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


/**
 * Generates a map where keys are given by keyMapper and values are given by valueMapper.
 * If any two elements would have the same key returned by [keyMapper]
 * then merge function is used to merge the values of such keys.
 * T: type of Iterable elements
 * K: type of keys
 * R: type of values
 *
 * @return Map<K,R>
 */
private inline fun <T, K, R> Iterable<T>.toMapWithMerge(
    crossinline keyMapper: (T) -> K,
    valueMapper: (T) -> R,
    merge: (R, R) -> R
): Map<K, R>{
    return groupingBy(keyMapper).aggregate { key, accumulator: R?, element, first ->
        if(accumulator == null) valueMapper(element)
        else merge(accumulator, valueMapper(element))
    }
}