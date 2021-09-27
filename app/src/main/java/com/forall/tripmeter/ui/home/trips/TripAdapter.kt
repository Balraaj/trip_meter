package com.forall.tripmeter.ui.home.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
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

class TripAdapter(private val unitMiles: Boolean): ListAdapter<Trip, TripAdapter.ViewHolder>(TripDiffer()) {

    private companion object{
        private const val LABEL_KMPH = "KMPH"
        private const val LABEL_MPH = "MPH"
        private const val LABEL_KM = "KM"
        private const val LABEL_MILES = "Miles"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(unitMiles, currentItem)
    }

    class ViewHolder private constructor(private val binding: ListItemTripsBinding): RecyclerView.ViewHolder(binding.root){
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

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListItemTripsBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

/** Diff utils implementation for efficient data loads */
class TripDiffer: DiffUtil.ItemCallback<Trip>(){
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip) = oldItem.tripId == newItem.tripId
    override fun areContentsTheSame(oldItem: Trip, newItem: Trip) = oldItem == newItem
}