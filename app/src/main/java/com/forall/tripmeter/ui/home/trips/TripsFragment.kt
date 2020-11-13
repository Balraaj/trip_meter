package com.forall.tripmeter.ui.home.trips

import android.view.View
import androidx.lifecycle.Observer
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.di.component.FragmentComponent
import com.forall.tripmeter.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_trips.*

class TripsFragment: BaseFragment<HomeViewModel>() {
    override fun provideLayoutId() = R.layout.fragment_trips
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)
    override fun setupView(view: View) {
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.getAllTrips().observe(this, Observer { updateUI(it) })
    }

    /**
     * Updates the UI with trip data. any currently active trip is not displayed.
     * if there are no trips then user is notified with appropriate text message.
     * @author Balraj
     */
    private fun updateUI(trips: List<Trip>){
        val visibleTrips = arrayListOf<Trip>()
        if(viewModel.tripActive.value!!) { visibleTrips.addAll(trips); visibleTrips.removeAt(0) }
        else { visibleTrips.addAll(trips) }
        if(visibleTrips.isEmpty()){
            rv_trips.visibility = View.GONE
            label_no_trips.visibility = View.VISIBLE
        }
        else{
            rv_trips.visibility = View.VISIBLE
            label_no_trips.visibility = View.GONE
            rv_trips.adapter = TripAdapter(visibleTrips)
        }
    }
}