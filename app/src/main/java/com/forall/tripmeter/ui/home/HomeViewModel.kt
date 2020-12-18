package com.forall.tripmeter.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.forall.tripmeter.base.BaseViewModel
import com.forall.tripmeter.common.Constants.EMPTY_STRING
import com.forall.tripmeter.common.Constants.LAST_LOCATION
import com.forall.tripmeter.common.Constants.SPEED_CACHE_SIZE
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
     * Signifies if a new trip is active.
     * this value is only changed when user toggles the trip start button.
     */
    var tripActive: MutableLiveData<Boolean> = MutableLiveData(false)
    var insertTrip: MutableLiveData<Boolean> = MutableLiveData(false)

    val currentTripDelta: MutableLiveData<Trip> = MutableLiveData()
    val averageSpeed: MutableLiveData<String> = MutableLiveData()
    private val locationData: MutableList<TripLocation> = LinkedList()

    val gpsLockAcquired: MutableLiveData<Boolean> = MutableLiveData(false)


    fun setTripActive(value: Boolean) { repo.isTripActive(value) }

    fun getLastKnownLocation(): LiveData<TripLocation> = repo.getLastKnownLocation()
    fun getLastKnownLocationNotLive(): TripLocation = repo.getLastKnownLocationNotLive()


    /**
     * Updates the speed data as well as trip delta with new location information.
     * this function is responsible for notifying the UI of changes.
     * @author Balraj
     */
    fun postNewLocation(location: TripLocation){
        updateSpeedData(location)
        updateTripDelta(location)
    }

    private fun updateSpeedData(location: TripLocation){
        if(locationData.size == SPEED_CACHE_SIZE) { locationData.removeAt(0) }
        locationData.add(location)
        postUpdatedSpeedDataToUI()
    }

    /**
     * Update the delta with current location information.
     */
    private fun updateTripDelta(location: TripLocation) {
        currentTripDelta.postValue(Trip.defaultDelta(location))
    }

    private fun setGPSLockAcquired(){
        if(gpsLockAcquired.value == null || gpsLockAcquired.value == false) {
            gpsLockAcquired.postValue(true)
        }
    }

    /**
     * Posts the updated speed information in LiveDat object so that
     * UI can update itself.
     *
     * @author Balraj
     */
    private fun postUpdatedSpeedDataToUI() = viewModelScope.launch {
        if(locationData.size < SPEED_CACHE_SIZE) { averageSpeed.postValue(EMPTY_STRING) }
        else{
            val sum = locationData.asSequence().sumBy { it.speed.toInt() }
            val speed = (sum * 3.6) / locationData.size
            averageSpeed.postValue(speed.toInt().toString())
            setGPSLockAcquired()
        }
    }


    fun insertTrip() = GlobalScope.launch(Dispatchers.IO) {
        repo.insertTrip(currentTripDelta.value!!)
    }

    /**
     * Deletes the latest trip, this is done when newly created trip doesn't
     * satisfy any required constraint.
     * @author Balraj
     */
    fun deleteLatestTrip() = GlobalScope.launch(Dispatchers.IO){
        repo.deleteLatestTrip()
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
        val currentTripLocation = locationData[locationData.size - 1]
        val currentLocation = Location(LAST_LOCATION)
        currentLocation.latitude = currentTripLocation.lat
        currentLocation.longitude = currentTripLocation.lon
        val lastLocation = Location(LAST_LOCATION)
        lastLocation.latitude = trip.endLat
        lastLocation.longitude = trip.endLong
        val distance = currentLocation.distanceTo(lastLocation) + trip.distance
        val speed = if(distance > 200) {
            (distance / ((newDelta.startTime - trip.startTime) / 1000) * 3.6 ).toInt()
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