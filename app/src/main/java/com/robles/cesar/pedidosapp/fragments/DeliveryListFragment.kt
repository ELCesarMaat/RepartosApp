package com.robles.cesar.pedidosapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.cesar.pedidosapp.NuevoRepartidorActivity
import com.robles.cesar.pedidosapp.R
import com.robles.cesar.pedidosapp.adapters.DeliveryAdapter
import com.robles.cesar.pedidosapp.databinding.FragmentDeliveryListBinding
import com.robles.cesar.pedidosapp.models.Delivery

class DeliveryListFragment : Fragment() {
    private val deliveryList = mutableListOf<Delivery>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var b: FragmentDeliveryListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentDeliveryListBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = GridLayoutManager(context, 1)
        val decorator = DividerItemDecoration(context, manager.orientation)
        b.recyclerListDeliveries.adapter = DeliveryAdapter(deliveryList)
        b.recyclerListDeliveries.layoutManager = manager
        b.recyclerListDeliveries.addItemDecoration(decorator)
        getDeliveries()

        b.btnNuevoRepartidor.setOnClickListener {
            val i = Intent(context, NuevoRepartidorActivity::class.java)
            startActivity(i)
        }
    }

    private fun getDeliveries() {
        db.collection("deliveries").orderBy("name").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (doc in it.result.documents) {
                        val id = doc.id
                        val name = doc.getString("name").toString()
                        val phone = doc.getString("phone").toString()
                        val email = doc.getString("email").toString()
                        val admin = doc.getBoolean("admin") ?: false
                        if(admin)
                            continue
                        val delivery = Delivery(id, name, phone, email, admin)
                        deliveryList.add(delivery)
                        b.recyclerListDeliveries.adapter?.notifyItemInserted(deliveryList.size)
                    }
                }
            }
    }

}