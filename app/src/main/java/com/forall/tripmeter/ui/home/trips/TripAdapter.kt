package com.forall.tripmeter.ui.home.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forall.tripmeter.R
import com.forall.tripmeter.database.entity.Trip
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

class TripAdapter(private var dataSet: List<Trip>): RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_trips, parent, false)
        return ViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        val v = holder.itemView
        v.tv_start_address.text = currentItem.startAddress
        v.tv_end_address.text = currentItem.endAddress
        v.tv_speed.text = currentItem.speed.toString()
        v.tv_distance.text = currentItem.distance.toString()
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
}