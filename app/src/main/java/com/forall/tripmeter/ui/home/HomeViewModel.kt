package com.forall.tripmeter.ui.home

import androidx.lifecycle.MutableLiveData
import com.forall.tripmeter.base.BaseViewModel
import com.forall.tripmeter.prefs.TripMeterSharedPrefs

class HomeViewModel(sharedPrefs: TripMeterSharedPrefs): BaseViewModel(sharedPrefs) {
    val data: MutableLiveData<String> = MutableLiveData()
}