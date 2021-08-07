package com.forall.tripmeter.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.common.Constants.EMPTY_STRING
import com.forall.tripmeter.common.Constants.MINIMUM_TRIP_DISTANCE_METER
import com.forall.tripmeter.common.Constants.NA
import com.forall.tripmeter.common.Constants.ZERO
import com.forall.tripmeter.common.Constants.ZERO_FLOAT
import com.forall.tripmeter.common.Utils
import com.forall.tripmeter.databinding.FragmentHomeBinding
import com.forall.tripmeter.di.component.FragmentComponent


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private companion object {
        private val BTN_ACTIVE_TINT_LIST = ColorStateList.valueOf(Color.parseColor("#7A7A7A"))
        private val BTN_INACTIVE_TINT_LIST = ColorStateList.valueOf(Color.parseColor("#F9F969"))
        private val BTN_ACTIVE_TEXT_CLR = Color.parseColor("#F9F969")
        private val BTN_INACTIVE_TEXT_CLR = Color.parseColor("#474747")
        private const val TRIP_INFO_CARD_ANIMATION_DURATION = 600L
        private const val SPEED_PLACE_HOLDER = "---"
    }

    override fun provideBinding(inflater: LayoutInflater,
                                container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)

    override fun setupView(view: View) {
        binding.btnTripStart.setOnClickListener { viewModel.performTripStateToggle() }
    }

    override fun onResume() {
        super.onResume()
        setupUIForUserMeasurementUnit()
        viewModel.tripActive.value = viewModel.isTripActive()
        if(viewModel.tripActive.value != null && viewModel.tripActive.value!!){
            updateTripToggleButton(true)
            animateCard(binding.containerActiveTrip, true)
        }
        else{
            updateTripToggleButton(false)
            animateCard(binding.containerActiveTrip, false)
        }
    }

    private fun setupUIForUserMeasurementUnit(){
        viewModel.setMeasurementUnit()
        if(viewModel.unitMiles){
            binding.labelKm.text = getString(R.string.label_miles)
            binding.labelKmph.text = getString(R.string.label_miles_per_hour)
            binding.labelTripKmph.text = getString(R.string.label_miles_per_hour)
        }
        else{
            binding.labelKm.text = getString(R.string.label_km)
            binding.labelKmph.text = getString(R.string.label_km_ph)
            binding.labelTripKmph.text = getString(R.string.label_km_ph)
        }
    }

    override fun setupObservers() {
        super.setupObservers()

        /* Whenever LocationService posts a new location inform the UI */
        viewModel.getLastKnownLocation().observe(this, Observer {
            if(it != null && it.isNotDefaultLocation()){
                viewModel.postNewLocation(it)
            }
        })

        /* Average speed is calculated based on location cache we maintain */
        viewModel.averageSpeed.observe(this, Observer { binding.tvSpeed.text = it })

        viewModel.currentTripDelta.observe(this, Observer {
            if(it != null && viewModel.tripActive.value != null && viewModel.tripActive.value!!) {
                updateCurrentTripView()
                viewModel.updateCurrentTrip()
            }
            binding.tvCurrentAddressTop.text = it.startAddress ?: EMPTY_STRING
        })

        viewModel.tripActive.observe(this, Observer { if (it) updateUIForActiveTrip(true) })

        viewModel.tripEnded.observe(this, Observer {
            if(it){
                viewModel.tripEnded.value = false
                updateUIForActiveTrip(false)
                resetCurrentTripView()
                verifyTripConstraintsAndCommit()
            }
        })

        /* Only allow user to start trip if we have a GPS lock */
        viewModel.gpsLockAcquired.observe(this, Observer {
            if(it){
                animateCard(binding.containerGpsLock, false)
                binding.btnTripStart.isEnabled = true
                updateTripToggleButton(viewModel.tripActive.value?:false)
            }
        })
    }

    /**
     * Commit newly created trip only if it satisfies all the required constraints.
     */
    private fun verifyTripConstraintsAndCommit() {
        val trip = viewModel.getLatestTrip() ?: return
        if(trip.distance < MINIMUM_TRIP_DISTANCE_METER){
            viewModel.deleteLatestTrip()
            showNotificationDialog(getString(R.string.trip_not_commited)){}
        }
    }

    private fun updateUIForActiveTrip(tripActive: Boolean){
        updateTripToggleButton(tripActive)
        animateCard(binding.containerActiveTrip, tripActive)
    }

    /**
     * Updates the view of trip start button (text and background tint)
     * depending on the state of trip. (ACTIVE or INACTIVE)
     * @author Balraj
     */
    private fun updateTripToggleButton(tripActive: Boolean){
        binding.btnTripStart.text = getString(if(tripActive) R.string.end_trip else R.string.start_trip)
        val btnTint = if(tripActive) { BTN_ACTIVE_TINT_LIST } else { BTN_INACTIVE_TINT_LIST }
        val textColor = if(tripActive) { BTN_ACTIVE_TEXT_CLR } else { BTN_INACTIVE_TEXT_CLR }
        binding.btnTripStart.setTextColor(textColor)
        ViewCompat.setBackgroundTintList(binding.btnTripStart, btnTint)
    }

    /**
     * Shows an animation of trip info card as sliding from right
     * or collapsing into right side, depending on the state of trip.
     * @author Balraj
     */
    private fun animateCard(v: View, show: Boolean) {
        val transition = Slide(Gravity.RIGHT)
        transition.duration = TRIP_INFO_CARD_ANIMATION_DURATION
        transition.addTarget(v.id)
        TransitionManager.beginDelayedTransition(binding.rootFragmentHome, transition)
        v.visibility = if (show) View.VISIBLE else View.GONE
    }


    private fun updateCurrentTripView(){
        val t = viewModel.getLatestTrip() ?: return
        binding.tvTripStartTime.text = Utils.millisToTripTimeFormat(t.startTime)
        binding.tvStartAddress.text = t.startAddress
        binding.tvCurrentAddress.text = t.endAddress

        /* Set speed and distance based on the chosen measurement unit */
        if(viewModel.unitMiles) {
            binding.tvDistance.text = Utils.metersToMiles(t.distance).toString()
            binding.tvTripSpeed.text = Utils.kmphToMph(t.speed).toString()
        }
        else {
            binding.tvDistance.text = Utils.metersToKM(t.distance).toString()
            binding.tvTripSpeed.text = t.speed.toString()
        }
    }

    private fun resetCurrentTripView(){
        binding.tvTripStartTime.text = NA
        binding.tvStartAddress.text = EMPTY_STRING
        binding.tvCurrentAddress.text = EMPTY_STRING
        binding.tvDistance.text = ZERO_FLOAT.toString()
        binding.tvTripSpeed.text = ZERO.toString()
    }
}