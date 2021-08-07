package com.forall.tripmeter.ui.home.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forall.tripmeter.common.Utils
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.databinding.ListItemTripsBinding

/**
 * Project Name : Trip Meter
 *
 * @author Balraj
 * @date NOV 11, 2020
 * Copyright (c) 2020, Yamaha Motor Solution (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * TripAdapter : Adapter for the Trips screen.
 * -----------------------------------------------------------------------------------
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

class TripAdapter(private val unitMiles: Boolean,
                  private var dataSet: List<Trip>): RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    private companion object{
        private const val LABEL_KMPH = "KMPH"
        private const val LABEL_MPH = "MPH"
        private const val LABEL_KM = "KM"
        private const val LABEL_MILES = "Miles"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemTripsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        holder.bind(unitMiles, currentItem)
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(private val binding: ListItemTripsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(unitMiles: Boolean, trip: Trip){
            binding.tvTripStartTime.text = Utils.millisToTripTimeFormat(trip.startTime)
            binding.tvTripEndTime.text = Utils.millisToTripTimeFormat(trip.endTime)
            binding.tvStartAddress.text = trip.startAddress
            binding.tvEndAddress.text = trip.endAddress

            /* Set speed and distance based on the chosen measurement unit */
            if(unitMiles) {
                binding.labelKm.text = LABEL_MILES
                binding.labelKmph.text = LABEL_MPH
                binding.tvDistance.text = Utils.metersToMiles(trip.distance).toString()
                binding.tvSpeed.text = Utils.kmphToMph(trip.speed).toString()
            }
            else {
                binding.labelKm.text = LABEL_KM
                binding.labelKmph.text = LABEL_KMPH
                binding.tvDistance.text = Utils.metersToKM(trip.distance).toString()
                binding.tvSpeed.text = trip.speed.toString()
            }
        }
    }
}