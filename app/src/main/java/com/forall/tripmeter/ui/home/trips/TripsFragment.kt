package com.forall.tripmeter.ui.home.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.common.DistanceUnit
import com.forall.tripmeter.common.TripState
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.databinding.FragmentTripsBinding
import com.forall.tripmeter.di.component.FragmentComponent
import com.forall.tripmeter.ui.home.HomeViewModel

class TripsFragment: BaseFragment<HomeViewModel, FragmentTripsBinding>() {

    override fun provideBinding(inflater: LayoutInflater,
                                container: ViewGroup?): FragmentTripsBinding {
        return FragmentTripsBinding.inflate(inflater, container, false)
    }
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)

    override fun setupView(view: View) {
        viewModel.initTripState()
        viewModel.initDistanceUnit()
        initRecyclerView()
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.getAllTrips().observe(this, { updateUI(it) })
    }

    private fun initRecyclerView() {
        binding.rvTrips.adapter = TripAdapter(arrayListOf(), viewModel.distanceUnit.value ?: DistanceUnit.KM)
    }

    /**
     * Updates the UI with trip data. any currently active trip is not displayed.
     * if there are no trips then user is notified with appropriate text message.
     * @author Balraj
     */
    private fun updateUI(trips: List<Trip>){
        val visibleTrips = arrayListOf<Trip>()
        if(viewModel.tripState.value == TripState.ACTIVE) { visibleTrips.addAll(trips); visibleTrips.removeAt(0) }
        else { visibleTrips.addAll(trips) }
        if(visibleTrips.isEmpty()){
            binding.rvTrips.visibility = View.GONE
            binding.labelNoTrips.visibility = View.VISIBLE
        }
        else{
            binding.rvTrips.visibility = View.VISIBLE
            binding.labelNoTrips.visibility = View.GONE
            binding.rvTrips.adapter = TripAdapter(visibleTrips, viewModel.distanceUnit.value ?: DistanceUnit.KM)
        }
    }
}