package com.forall.tripmeter.prefs

import android.content.SharedPreferences

class TripMeterSharedPrefs(private val prefs: SharedPreferences) {

    private companion object {
        private const val IS_TRIP_ACTIVE = "IS_TRIP_ACTIVE"
    }

    var isTripActive: Boolean
        get() = prefs.getBoolean(IS_TRIP_ACTIVE, false)
        set(value){ prefs.edit().putBoolean(IS_TRIP_ACTIVE, value).commit() }


}