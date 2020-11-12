package com.forall.tripmeter.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.forall.tripmeter.repository.Repository

abstract class BaseViewModel(protected val repo: Repository):ViewModel() {
    val messageStringId: MutableLiveData<Int> = MutableLiveData()
    val messageString: MutableLiveData<String> = MutableLiveData()
}