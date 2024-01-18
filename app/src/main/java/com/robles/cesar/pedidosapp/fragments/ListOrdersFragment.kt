package com.robles.cesar.pedidosapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.robles.cesar.pedidosapp.R
import com.robles.cesar.pedidosapp.adapters.OrderAdapter
import com.robles.cesar.pedidosapp.databinding.FragmentListOrdersBinding
import com.robles.cesar.pedidosapp.models.Order
import com.robles.cesar.pedidosapp.models.OrderStatus
import java.lang.Exception

class ListOrdersFragment : Fragment() {
    lateinit var b: FragmentListOrdersBinding
    val db = FirebaseFirestore.getInstance()
    val orderList = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentListOrdersBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = LinearLayoutManager(context)
        val decorator = DividerItemDecoration(context, manager.orientation)
        b.recyclerListOrders.layoutManager = manager
        b.recyclerListOrders.addItemDecoration(decorator)

        b.txtSearchOrder.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        getOrders()
    }

    private fun getOrders() {
        orderList.clear()
        b.recyclerListOrders.adapter = OrderAdapter(orderList){

        }
        db.collection("orders").get().addOnCompleteListener {
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
                orderList.sortBy { order -> order.status }
                b.recyclerListOrders.adapter?.notifyDataSetChanged()
            }

        }
    }

}