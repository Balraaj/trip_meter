package com.forall.tripmeter.common

import com.forall.tripmeter.common.Constants.FACTOR_METER_TO_KM
import com.forall.tripmeter.common.Constants.FACTOR_METER_TO_MILES

enum class DistanceUnit(val conversionFactor: Double) {
    MILES(FACTOR_METER_TO_MILES),
    KM(FACTOR_METER_TO_KM)
}