package com.robles.cesar.pedidosapp.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.robles.cesar.pedidosapp.databinding.ItemDeliveryBinding
import com.robles.cesar.pedidosapp.models.Delivery

class DeliveryViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private val b = ItemDeliveryBinding.bind(view)
    fun render(delivery: Delivery){
        b.txtDeliveryName.text = delivery.name
        b.txtDeliveryPhone.text = delivery.phone
    }
}