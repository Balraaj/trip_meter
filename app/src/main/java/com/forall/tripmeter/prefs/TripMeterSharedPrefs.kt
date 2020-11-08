package com.forall.tripmeter.prefs

import android.content.SharedPreferences

class TripMeterSharedPrefs(private val prefs: SharedPreferences) {

    fun write() = prefs.edit().putString("WHAT", "MOTHERFUCKER").apply()
    fun read() = prefs.getString("WHAT", "")
}