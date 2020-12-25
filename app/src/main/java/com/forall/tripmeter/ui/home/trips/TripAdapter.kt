package com.forall.tripmeter.ui.home.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forall.tripmeter.R
import com.forall.tripmeter.common.Utils
import com.forall.tripmeter.common.inKmph
import com.forall.tripmeter.common.inMiles
import com.forall.tripmeter.database.entity.Trip
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.list_item_trips.view.*

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
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_trips, parent, false)
        return ViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        val v = holder.itemView
        v.tv_trip_start_time.text = Utils.millisToTripTimeFormat(currentItem.startTime)
        v.tv_trip_end_time.text = Utils.millisToTripTimeFormat(currentItem.endTime)
        v.tv_start_address.text = currentItem.startAddress
        v.tv_end_address.text = currentItem.endAddress

        /* Set speed and distance based on the chosen measurement unit */
        if(unitMiles) {
            v.label_km.text = LABEL_MILES
            v.label_kmph.text = LABEL_MPH
            v.tv_distance.text = Utils.metersToMiles(currentItem.distance).toString()
            v.tv_speed.text = Utils.kmphToMph(currentItem.speed).toString()
        }
        else {
            v.label_km.text = LABEL_KM
            v.label_kmph.text = LABEL_KMPH
            v.tv_distance.text = Utils.metersToKM(currentItem.distance).toString()
            v.tv_speed.text = currentItem.speed.toString()
        }
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
}