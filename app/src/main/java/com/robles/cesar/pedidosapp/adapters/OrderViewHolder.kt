package com.robles.cesar.pedidosapp.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.robles.cesar.pedidosapp.databinding.ItemOrderBinding
import com.robles.cesar.pedidosapp.models.Order
import com.robles.cesar.pedidosapp.models.OrderStatus

class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val b = ItemOrderBinding.bind(view)
    fun render(order: Order, onCLickListener: (Order) -> Unit) {
        b.txtCustomerName.text = order.customerName
        b.txtCustomerAdress.text = order.customerAddress
        b.txtDetails.text = order.orderDetails
        b.txtOrderId.text = order.orderId

        when (order.status) {
            OrderStatus.ENTREGADO -> itemView.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFCDFFC4"))

            OrderStatus.EN_REPARTO -> itemView.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFFEFFC8"))

            OrderStatus.PENDIENTE -> itemView.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFFFEEEE"))
        }

        itemView.setOnClickListener {
            onCLickListener(order)
        }
    }
}