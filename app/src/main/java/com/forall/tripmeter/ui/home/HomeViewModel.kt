package com.forall.tripmeter.ui.home

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.forall.tripmeter.base.BaseViewModel
import com.forall.tripmeter.common.Constants.EMPTY_STRING
import com.forall.tripmeter.prefs.TripMeterSharedPrefs
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(sharedPrefs: TripMeterSharedPrefs): BaseViewModel(sharedPrefs) {
    private val locationData: MutableList<Location> = LinkedList()
    val averageSpeed: MutableLiveData<String> = MutableLiveData()

    fun updateLocationData(location: Location){
        if(locationData.size == 4) { locationData.removeAt(0) }
        locationData.add(location)
        postUpdatedDataToUI()
    }

    private fun postUpdatedDataToUI() = viewModelScope.launch {
        if(locationData.size < 4) { averageSpeed.postValue(EMPTY_STRING) }
        else{
            val sum = locationData.asSequence().sumBy { it.speed.toInt() }
            val speed = (sum * 3.6) / locationData.size
            averageSpeed.postValue(speed.toInt().toString())
        }
    }

    fun getLatestSpeed(): String{
        return if(locationData.isEmpty()){
            EMPTY_STRING
        } else {
            (locationData[locationData.size-1].speed * 3.6).toInt().toString()
        }
    }
}