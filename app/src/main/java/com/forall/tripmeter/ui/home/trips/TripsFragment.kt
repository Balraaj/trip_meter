package com.forall.tripmeter.ui.home.trips

import android.view.View
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.common.Trip
import com.forall.tripmeter.di.component.FragmentComponent
import com.forall.tripmeter.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_trips.*

class TripsFragment: BaseFragment<HomeViewModel>() {
    override fun provideLayoutId() = R.layout.fragment_trips
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)
    override fun setupView(view: View) {
        rv_trips.adapter = TripAdapter(getDummyTrips())
    }


    private fun getDummyTrips(): List<Trip>{
        val trip1 = Trip("Ward no. 13, Dholipal, Hanumangarh, Rajasthan",
        "Near Bhagat singh chowk, Hanumangarh juction, Hanumangarh, Rajasthan", 75)

        val trip2 = Trip("Ward no. 7, 7A choti, Sriganganagar, Rajasthan",
            "Near Bhagat singh chowk, Sriganganagar, Rajasthan", 37)

        val trip3 = Trip("Near Hotel Taj, Mumbai, Maharashtra",
            "Chuna fatak, Hanumangarh, Rajasthan", 89)

        val trip4 = Trip("Public park, opposite forest department office, Bikaner, Rajasthan",
            "96A, Sector 11-D, Faridabad, Haryana", 105)

        return listOf(trip1, trip2, trip3, trip4)
    }
}