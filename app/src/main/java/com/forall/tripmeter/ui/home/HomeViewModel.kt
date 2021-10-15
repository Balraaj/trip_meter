package com.forall.tripmeter.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseViewModel
import com.forall.tripmeter.common.*
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.database.entity.TripLocation
import com.forall.tripmeter.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(repo: Repository): BaseViewModel(repo) {

    var permissionAllowed = false

    /**
     * Properties to store measurement factor information,
     * depending on the Kmph/Mph we need different factors and conversions
     */
    private val _distanceUnit: MutableLiveData<DistanceUnit> = MutableLiveData(DistanceUnit.KM)
    val distanceUnit: LiveData<DistanceUnit> = _distanceUnit

    private val _averageSpeed: MutableLiveData<String> = MutableLiveData()
    val averageSpeed: LiveData<String> = _averageSpeed

    private val _gpsLockAcquired: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val gpsLockAcquired: LiveData<Event<Boolean>> = _gpsLockAcquired


    /**
     * Signifies if a new trip is active.
     * this value is only changed when user toggles the trip start button.
     */
    private val _tripState: MutableLiveData<TripState> = MutableLiveData(TripState.ENDED)
    val tripState: LiveData<TripState> = _tripState

    fun initDistanceUnit(){
        val unit = if(repo.isMeasurementUnitMiles()) DistanceUnit.MILES else DistanceUnit.KM
        _distanceUnit.postValue(unit)
    }

    fun initTripState() {
        val currentState = if(isTripActive()) TripState.ACTIVE else TripState.ENDED
        _tripState.postValue(currentState)
    }

    private fun isTripActive() = repo.isTripActive()


    val currentTrip: LiveData<Trip> = Transformations.map(repo.getLatestTripLive()){
        if(it == null) return@map null
        val (distance, speed) = when(distanceUnit.value){
            DistanceUnit.KM -> { Pair(Utils.metersToKM(it.distance), it.speed.toFloat().inKmph()) }
            else ->            { Pair(Utils.metersToMiles(it.distance), it.speed.toFloat().inMiles()) }
        }
        it.copy(distance = distance, speed = speed)
    }

    val lastLocation: LiveData<TripLocation> = Transformations.map(repo.getLastKnownLocation()){
        val trip = it ?: return@map null
        setGpsLockAndAverageSpeedLiveData(it)
        return@map it
    }

    private fun setGpsLockAndAverageSpeedLiveData(lastLocation: TripLocation) =
        viewModelScope.launch(Dispatchers.Default) {
        if(_gpsLockAcquired.value == null) { _gpsLockAcquired.postValue(Event(true)) }
        setAverageSpeedLiveData(lastLocation.speed)
    }

    private fun setAverageSpeedLiveData(speed: Int){
        val newAvgSpeed = (speed * distanceUnit.value!!.conversionFactor).toInt()
        _averageSpeed.postValue(newAvgSpeed.toString())
    }

    fun performTripStateToggle() = viewModelScope.launch(Dispatchers.Default) {
        /* If current trip state is ended that means, a new trip have been started */
        if (TripState.ENDED == tripState.value) {
            insertTrip()
            repo.isTripActive(true)
            _tripState.postValue(TripState.ACTIVE)
        }
        else {
            repo.isTripActive(false)
            _tripState.postValue(TripState.ENDED)
            verifyTripConstraintsAndCommit()
        }
    }

    private fun insertTrip() = GlobalScope.launch(Dispatchers.IO) {
        repo.insertTrip(Trip.fromLocation(lastLocation.value!!))
    }


    /** Commit newly created trip only if it satisfies all the required constraints. */
    private fun verifyTripConstraintsAndCommit() = viewModelScope.launch {
        val trip = repo.getLatestTrip() ?: return@launch
        if(trip.distance < Constants.MINIMUM_TRIP_DISTANCE_METER){
            repo.deleteLatestTrip()
            showNotificationDialog.postValue(R.string.trip_not_commited)
        }
    }

    fun getAllTrips() = repo.getAllTrips()
}