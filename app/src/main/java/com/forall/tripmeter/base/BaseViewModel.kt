package com.forall.tripmeter.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.forall.tripmeter.prefs.TripMeterSharedPrefs

abstract class BaseViewModel(protected val sharedPrefs: TripMeterSharedPrefs):ViewModel() {
    val messageStringId: MutableLiveData<Int> = MutableLiveData()
    val messageString: MutableLiveData<String> = MutableLiveData()
}