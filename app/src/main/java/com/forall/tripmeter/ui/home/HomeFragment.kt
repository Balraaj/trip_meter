package com.forall.tripmeter.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.View
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
import com.forall.tripmeter.di.component.FragmentComponent
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<HomeViewModel>() {

    private companion object {
        private val BTN_ACTIVE_TINT_LIST = ColorStateList.valueOf(Color.parseColor("#7A7A7A"))
        private val BTN_INACTIVE_TINT_LIST = ColorStateList.valueOf(Color.parseColor("#F9F969"))
        private val BTN_ACTIVE_TEXT_CLR = Color.parseColor("#F9F969")
        private val BTN_INACTIVE_TEXT_CLR = Color.parseColor("#474747")
        private const val TRIP_INFO_CARD_ANIMATION_DURATION = 600L
        private const val SPEED_PLACE_HOLDER = "---"
    }

    override fun provideLayoutId() = R.layout.fragment_home
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)

    override fun setupView(view: View) {
        btn_trip_start.setOnClickListener {
            if(viewModel.tripActive.value != null){
                viewModel.tripActive.value = !viewModel.tripActive.value!!
                if(viewModel.tripActive.value!!) { viewModel.insertTrip.value = true }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.tripActive.value != null && viewModel.tripActive.value!!){
            updateTripToggleButton(true)
            animateCard(container_active_trip, true)
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.averageSpeed.observe(this, Observer {
            tv_speed.text = if(it == EMPTY_STRING) { SPEED_PLACE_HOLDER } else { it }
        })

        viewModel.currentTripDelta.observe(this, Observer {
            if(it != null && viewModel.tripActive.value != null && viewModel.tripActive.value!!) {
                updateCurrentTripView()
                viewModel.updateCurrentTrip()
            }
            tv_current_address_top.text = it.startAddress ?: EMPTY_STRING
        })

        viewModel.tripActive.observe(this, Observer {
            it?.let {
                updateUIForActiveTrip(it)
                if(!it) {
                    viewModel.setTripActive(false)
                    resetCurrentTripView()
                    verifyTripConstraintsAndCommit()
                }
            }
        })

        viewModel.insertTrip.observe(this, Observer {
            if(it) {
                viewModel.setTripActive(true)
                viewModel.insertTrip.value = false;
                viewModel.insertTrip()
            }
        })

        viewModel.gpsLockAcquired.observe(this, Observer {
            if(it){
                animateCard(container_gps_lock, false)
                btn_trip_start.isEnabled = true
                updateTripToggleButton(false)
            }
        })

        viewModel.getLastKnownLocation().observe(this, Observer {
            if(it != null && it.isNotDefaultLocation()){
                viewModel.postNewLocation(it)
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
        animateCard(container_active_trip, tripActive)
    }

    /**
     * Updates the view of trip start button (text and background tint)
     * depending on the state of trip. (ACTIVE or INACTIVE)
     * @author Balraj
     */
    private fun updateTripToggleButton(tripActive: Boolean){
        btn_trip_start.text = getString(if(tripActive) R.string.end_trip else R.string.start_trip)
        val btnTint = if(tripActive) { BTN_ACTIVE_TINT_LIST } else { BTN_INACTIVE_TINT_LIST }
        val textColor = if(tripActive) { BTN_ACTIVE_TEXT_CLR } else { BTN_INACTIVE_TEXT_CLR }
        btn_trip_start.setTextColor(textColor)
        ViewCompat.setBackgroundTintList(btn_trip_start, btnTint)
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
        TransitionManager.beginDelayedTransition(root_fragment_home, transition)
        v.visibility = if (show) View.VISIBLE else View.GONE
    }


    private fun updateCurrentTripView(){
        val trip = viewModel.getLatestTrip() ?: return
        tv_trip_start_time.text = Utils.millisToTripTimeFormat(trip.startTime)
        tv_start_address.text = trip.startAddress
        tv_current_address.text = trip.endAddress
        tv_distance.text = Utils.metersToKM(trip.distance).toString()
        tv_trip_speed.text = trip.speed.toString()
    }

    private fun resetCurrentTripView(){
        tv_trip_start_time.text = NA
        tv_start_address.text = EMPTY_STRING
        tv_current_address.text = EMPTY_STRING
        tv_distance.text = ZERO_FLOAT.toString()
        tv_trip_speed.text = ZERO.toString()
    }
}