package com.forall.tripmeter.ui.home

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.forall.tripmeter.base.BaseViewModel
import com.forall.tripmeter.common.Constants.EMPTY_STRING
import com.forall.tripmeter.common.Constants.LAST_LOCATION
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(repo: Repository): BaseViewModel(repo) {

    /**
     * Signifies if a new trip is active.
     * this value is only changed when user toggles the trip start button.
     */
    var tripActive = false

    val currentTripDelta: MutableLiveData<Trip> = MutableLiveData()
    val averageSpeed: MutableLiveData<String> = MutableLiveData()
    private val locationData: MutableList<Location> = LinkedList()


    /**
     * Updates the speed data as well as trip delta with new location information.
     * this function is responsible for notifying the UI of changes.
     * @author Balraj
     */
    fun postNewLocation(location: Location, address: String){
        updateSpeedData(location)
        updateTripDelta(location, address)
    }

    private fun updateSpeedData(location: Location){
        if(locationData.size == 4) { locationData.removeAt(0) }
        locationData.add(location)
        postUpdatedSpeedDataToUI()
    }

    /**
     * Update the delta with current location information.
     */
    private fun updateTripDelta(location: Location, address: String) {
        currentTripDelta.postValue(Trip.defaultDelta(location, address))
    }

    /**
     * Posts the updated speed information in LiveDat object so that
     * UI can update itself.
     *
     * @author Balraj
     */
    private fun postUpdatedSpeedDataToUI() = viewModelScope.launch {
        if(locationData.size < 4) { averageSpeed.postValue(EMPTY_STRING) }
        else{
            val sum = locationData.asSequence().sumBy { it.speed.toInt() }
            val speed = (sum * 3.6) / locationData.size
            averageSpeed.postValue(speed.toInt().toString())
        }
    }


    fun insertTrip() = GlobalScope.launch(Dispatchers.IO) {
        repo.insertTrip(currentTripDelta.value!!)
    }

    /**
     * Updates the currently active trip with the new location information.
     * Following fields of current trip are updated:
     *      1. distance (add new distance)
     *      2. speed (calculate new average)
     *      3. endLat
     *      4. endLong
     *      5. endTime
     *      6. endAddress
     *
     * @author Balraj
     */
    fun updateCurrentTrip() = GlobalScope.launch(Dispatchers.IO) {
        val trip = repo.getLatestTrip()
        val newDelta = currentTripDelta.value
        if(trip == null || newDelta == null) { return@launch }
        val (distance, speed) = getDistanceAndSpeed(trip, newDelta)
        trip.updateWithDelta(distance, speed, newDelta)
        repo.updateCurrentTrip(trip)
    }

    /**
     * Returns distance and speed of the current trip after incorporating
     * the new location information.
     * @author Balraj
     */
    private fun getDistanceAndSpeed(trip: Trip, newDelta: Trip): Pair<Float, Int>{
        val currentLocation = locationData[locationData.size - 1]
        val lastLocation = Location(LAST_LOCATION)
        lastLocation.latitude = trip.endLat
        lastLocation.longitude = trip.endLong
        val distance = currentLocation.distanceTo(lastLocation)
        val speed = if(distance > 1000) {
            (distance / ((newDelta.startTime - trip.endTime) / 1000) * 3.6 ).toInt()
        } else { 0 }
        return Pair(distance, speed)
    }

    fun getLatestTrip() = repo.getLatestTrip()

    fun getAllTrips() = repo.getAllTrips()


    fun getLatestSpeed(): String{
        return if(locationData.isEmpty()) { EMPTY_STRING }
        else {
            (locationData[locationData.size-1].speed * 3.6).toInt().toString()
        }
    }

}