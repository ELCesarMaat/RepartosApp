package com.robles.cesar.pedidosapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robles.cesar.pedidosapp.R
import com.robles.cesar.pedidosapp.models.Delivery

class DeliveryAdapter(private val deliveryList: List<Delivery>) : RecyclerView.Adapter<DeliveryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DeliveryViewHolder(layoutInflater.inflate(R.layout.item_delivery, parent, false))

    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        val item = deliveryList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return deliveryList.size
    }
}