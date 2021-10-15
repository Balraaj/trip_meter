package com.forall.tripmeter.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.common.Constants.EMPTY_STRING
import com.forall.tripmeter.common.Constants.NA
import com.forall.tripmeter.common.Constants.ZERO
import com.forall.tripmeter.common.Constants.ZERO_FLOAT
import com.forall.tripmeter.common.DistanceUnit
import com.forall.tripmeter.common.TripState
import com.forall.tripmeter.common.Utils
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.databinding.FragmentHomeBinding
import com.forall.tripmeter.di.component.FragmentComponent


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private companion object {
        private val BTN_ACTIVE_TINT_LIST = ColorStateList.valueOf(Color.parseColor("#7A7A7A"))
        private val BTN_INACTIVE_TINT_LIST = ColorStateList.valueOf(Color.parseColor("#F9F969"))
        private val BTN_ACTIVE_TEXT_CLR = Color.parseColor("#F9F969")
        private val BTN_INACTIVE_TEXT_CLR = Color.parseColor("#474747")
        private const val TRIP_INFO_CARD_ANIMATION_DURATION = 600L
    }

    override fun provideBinding(inflater: LayoutInflater,
                                container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)

    override fun setupView(view: View) {
        viewModel.initTripState()
        viewModel.initDistanceUnit()
        binding.btnTripStart.setOnClickListener { viewModel.performTripStateToggle() }
    }

    override fun setupObservers() {
        super.setupObservers()

        /* Whenever LocationService posts a new location update the current address */
        viewModel.lastLocation.observe(this, { lastLocation ->
            lastLocation?.address?.let { binding.tvCurrentAddressTop.text = it }
        })

        /* Average speed is calculated based on location cache we maintain */
        viewModel.averageSpeed.observe(this, { binding.tvSpeed.text = it })


        /* Only allow user to start trip if we have a GPS lock */
        viewModel.gpsLockAcquired.observe(this, {
            animateCard(binding.containerGpsLock, false)
            binding.btnTripStart.isEnabled = true
            updateTripToggleButton(tripActive = viewModel.tripState.value == TripState.ACTIVE)
        })

        /* Show/Hide active trip card and change trip toggle button UI based on trip state */
        viewModel.tripState.observe(this, { tripState ->
            if(TripState.ACTIVE == tripState){
                resetCurrentTripView()
                viewModel.currentTrip.observe(this){ it?.let { updateCurrentTripView(it) } }
                updateUIForActiveTrip(tripActive = true)
                updateTripToggleButton(tripActive = true)
                animateCard(binding.containerActiveTrip, show = true)
            }
            else{
                viewModel.currentTrip.removeObservers(this@HomeFragment)
                updateUIForActiveTrip(tripActive = false)
                updateTripToggleButton(tripActive = false)
                animateCard(binding.containerActiveTrip, show = false)
            }
        })

        /* Update the distance unit labels (kmph/miles) based on distance unit value */
        viewModel.distanceUnit.observe(this){
            if(DistanceUnit.MILES == it){
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

    /**
     * Updates the view of trip start button (text and background tint)
     * depending on the state of trip. (ACTIVE or INACTIVE)
     * @author Balraj
     */
    private fun updateTripToggleButton(tripActive: Boolean){
        binding.btnTripStart.text = getString(if(tripActive) R.string.end_trip else R.string.start_trip)
        val (btnTint, textColor) = if(tripActive) { Pair(BTN_ACTIVE_TINT_LIST, BTN_ACTIVE_TEXT_CLR) }
                                   else           { Pair(BTN_INACTIVE_TINT_LIST, BTN_INACTIVE_TEXT_CLR) }
        binding.btnTripStart.setTextColor(textColor)
        ViewCompat.setBackgroundTintList(binding.btnTripStart, btnTint)
    }

    private fun updateUIForActiveTrip(tripActive: Boolean){
        updateTripToggleButton(tripActive)
        animateCard(binding.containerActiveTrip, tripActive)
    }

    /************************** FOLLOWING FUNCTIONS UPDATE THE CURRENT TRIP UI ********************/

    private fun updateCurrentTripView(currentTrip: Trip) {
        binding.tvTripStartTime.text  = Utils.millisToTripTimeFormat(currentTrip.startTime)
        binding.tvStartAddress.text   = currentTrip.startAddress
        binding.tvCurrentAddress.text = currentTrip.endAddress
        binding.tvDistance.text       = currentTrip.distance.toString()
        binding.tvTripSpeed.text      = currentTrip.speed.toString()
    }

    private fun resetCurrentTripView(){
        binding.tvTripStartTime.text    = NA
        binding.tvStartAddress.text     = EMPTY_STRING
        binding.tvCurrentAddress.text   = EMPTY_STRING
        binding.tvDistance.text         = ZERO_FLOAT.toString()
        binding.tvTripSpeed.text        = ZERO.toString()
    }
}