package com.robles.cesar.pedidosapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.robles.cesar.pedidosapp.R
import com.robles.cesar.pedidosapp.models.Order

class OrderAdapter(private var orderList: List<Order>, private val onCLickListener:(Order) -> Unit) : RecyclerView.Adapter<OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return OrderViewHolder(layoutInflater.inflate(R.layout.item_order, parent, false))

    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = orderList[position]
        holder.render(item, onCLickListener)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun updateList(newList: List<Order>){
        val orderListDiff = OrderDiffUtil(orderList, newList)
        val result = DiffUtil.calculateDiff(orderListDiff)
        orderList = newList
        result.dispatchUpdatesTo(this)
    }

}