package com.forall.tripmeter.common

class Event<T> (private val data: T){
    private var isConsumed = false
    fun get(): T? = if(isConsumed) null else { isConsumed = true; data }
}