package com.forall.tripmeter.ui.home

import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.common.Constants.EMPTY_STRING
import com.forall.tripmeter.di.component.FragmentComponent
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<HomeViewModel>() {

    private var btnColorActive = 0
    private var btnColorInactive = 0

    override fun provideLayoutId() = R.layout.fragment_home
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)

    override fun setupView(view: View) {
        btnColorActive = ContextCompat.getColor(requireContext(), R.color.trip_toggle_active)
        btnColorInactive = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        btn_trip_start.setOnClickListener(tripToggleListener)
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.averageSpeed.observe(this, Observer {
            tv_speed.text = if(it == EMPTY_STRING) { "---" } else { it }
            tv_speed_dummy.text = viewModel.getLatestSpeed()
        })

        viewModel.currentTripDelta.observe(this, Observer {
            if(it != null && viewModel.tripActive) {
                updateCurrentTripView()
                viewModel.updateCurrentTrip()
            }
        })
    }

    private val tripToggleListener = View.OnClickListener {
        if(container_active_trip.visibility == View.VISIBLE){
            ViewCompat.setBackgroundTintList(btn_trip_start, ColorStateList.valueOf(btnColorInactive))
            btn_trip_start.text = "START"
            toggle(false)
            viewModel.tripActive = false
        }
        else{
            ViewCompat.setBackgroundTintList(btn_trip_start, ColorStateList.valueOf(btnColorActive))
            btn_trip_start.text = "END"
            toggle(true)
            viewModel.tripActive = true
            viewModel.insertTrip()
        }
    }

    private fun toggle(show: Boolean) {
        val transition = Slide(Gravity.RIGHT)
        transition.duration = 600
        transition.addTarget(R.id.container_active_trip)
        TransitionManager.beginDelayedTransition(root_fragment_home, transition)
        container_active_trip.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun updateCurrentTripView(){
        val trip = viewModel.getLatestTrip() ?: return
        tv_start_address.text = trip.startAddress
        tv_end_address.text = trip.endAddress
        tv_trip_speed.text = trip.speed.toString()
    }
}