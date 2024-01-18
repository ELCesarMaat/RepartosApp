package com.robles.cesar.pedidosapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.cesar.pedidosapp.OrderInfoActivity
import com.robles.cesar.pedidosapp.adapters.OrderAdapter
import com.robles.cesar.pedidosapp.databinding.FragmentDeliveryOrdersBinding
import com.robles.cesar.pedidosapp.models.Order
import com.robles.cesar.pedidosapp.models.OrderStatus
import java.lang.Exception


class DeliveryOrders : Fragment() {
    private lateinit var b: FragmentDeliveryOrdersBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val orderList = arrayListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentDeliveryOrdersBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = LinearLayoutManager(context)
        val decorator = DividerItemDecoration(context, manager.orientation)
        b.rvDeliveryOrders.layoutManager = manager
        b.rvDeliveryOrders.addItemDecoration(decorator)
        b.rvDeliveryOrders.adapter = OrderAdapter(orderList){
            onItemOrderClick(it)
        }
    }

    private fun onItemOrderClick(order: Order) {
         showOrderInfo(order.orderId)
    }

    private fun showOrderInfo(orderId: String) {
        val i = Intent(context, OrderInfoActivity::class.java)
        i.putExtra("order_id", orderId)
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        getDeliveryOrders()
    }

    private fun getDeliveryOrders() {
        orderList.clear()
        db.collection("orders").whereEqualTo("deliveryId", auth.currentUser?.uid).get().addOnCompleteListener {
            if (!it.isSuccessful) {
                Toast.makeText(context, "Error ${it.exception?.message}", Toast.LENGTH_SHORT).show()
            } else {
                for (doc in it.result.documents) {
                    val orderId = doc.id
                    val customerName = doc.get("customerName").toString()
                    val customerAddress = doc.get("customerAddress").toString()
                    val customerPhone = doc.get("customerPhone").toString()
                    val orderDetails = doc.get("orderDetails").toString()
                    val status = try {
                        doc.get("status") as Long? ?: OrderStatus.PENDIENTE.code
                    } catch (e: Exception) {
                        OrderStatus.PENDIENTE.code
                    }
                    val deliveryId = doc.get("deliveryId").toString()
                    val order = Order(
                        orderId,
                        customerName,
                        customerPhone,
                        customerAddress,
                        orderDetails,
                        deliveryId,
                        OrderStatus.fromInt(status)
                    )
                    orderList.add(order)
                }
                orderList.sortBy { x -> x.status }
                b.rvDeliveryOrders.adapter?.notifyDataSetChanged()
            }
        }
    }
}