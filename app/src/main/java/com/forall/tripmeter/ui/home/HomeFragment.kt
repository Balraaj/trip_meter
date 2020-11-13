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
import com.forall.tripmeter.common.Constants.ZERO
import com.forall.tripmeter.common.Constants.ZERO_FLOAT
import com.forall.tripmeter.common.Utils
import com.forall.tripmeter.di.component.FragmentComponent
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<HomeViewModel>() {

    private companion object {
        private val BTN_ACTIVE_TINT_LIST = ColorStateList.valueOf(Color.parseColor("#19FDD6"))
        private val BTN_INACTIVE_TINT_LIST = ColorStateList.valueOf(Color.parseColor("#19DAFD"))
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
            animateTripInfoCard(true)
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
                if(!it) { resetCurrentTripView() }
            }
        })

        viewModel.insertTrip.observe(this, Observer {
            if(it) { viewModel.insertTrip.value = false; viewModel.insertTrip() }
        })
    }

    private fun updateUIForActiveTrip(tripActive: Boolean){
        updateTripToggleButton(tripActive)
        animateTripInfoCard(tripActive)
    }

    /**
     * Updates the view of trip start button (text and background tint)
     * depending on the state of trip. (ACTIVE or INACTIVE)
     * @author Balraj
     */
    private fun updateTripToggleButton(tripActive: Boolean){
        btn_trip_start.text = getString(if(tripActive) R.string.end_trip else R.string.start_trip)
        val btnTint = if(tripActive) { BTN_ACTIVE_TINT_LIST } else { BTN_INACTIVE_TINT_LIST }
        ViewCompat.setBackgroundTintList(btn_trip_start, btnTint)
    }

    /**
     * Shows an animation of trip info card as sliding from right
     * or collapsing into right side, depending on the state of trip.
     * @author Balraj
     */
    private fun animateTripInfoCard(tripActive: Boolean) {
        val transition = Slide(Gravity.RIGHT)
        transition.duration = TRIP_INFO_CARD_ANIMATION_DURATION
        transition.addTarget(R.id.container_active_trip)
        TransitionManager.beginDelayedTransition(root_fragment_home, transition)
        container_active_trip.visibility = if (tripActive) View.VISIBLE else View.GONE
    }


    private fun updateCurrentTripView(){
        val trip = viewModel.getLatestTrip() ?: return
        tv_start_address.text = trip.startAddress
        tv_current_address.text = trip.endAddress
        tv_distance.text = Utils.metersToKM(trip.distance).toString()
        tv_trip_speed.text = trip.speed.toString()
    }

    private fun resetCurrentTripView(){
        tv_start_address.text = EMPTY_STRING
        tv_current_address.text = EMPTY_STRING
        tv_distance.text = ZERO_FLOAT.toString()
        tv_trip_speed.text = ZERO.toString()
    }
}