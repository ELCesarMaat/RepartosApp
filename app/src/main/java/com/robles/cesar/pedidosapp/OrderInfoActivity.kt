package com.robles.cesar.pedidosapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.cesar.pedidosapp.databinding.ActivityOrderInfoBinding
import com.robles.cesar.pedidosapp.models.OrderStatus
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrderInfoActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val NO_LOCATION = "No se proporcionó ubicación"
    private var orderId: String = ""
    private lateinit var b: ActivityOrderInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityOrderInfoBinding.inflate(layoutInflater)
        setContentView(b.root)
        orderId = intent.getStringExtra("order_id") ?: ""
        if (orderId.isEmpty())
            Toast.makeText(
                this,
                "Error al obtener el ID ¿¿Esto no debio pasar??",
                Toast.LENGTH_SHORT
            ).show()
        else
            getOrderInfo(orderId)

        b.tvLocation.setOnClickListener {
            val url = b.tvLocation.text.toString()
            if (url != NO_LOCATION) {
                openMap(url)
            }
        }

        b.tvDeliveringLocation.setOnClickListener {
            val url = b.tvDeliveringLocation.text.toString()
            if (url != NO_LOCATION && (url.contains("//") || url.lowercase().contains("http")))
                openMap(url)
        }

        b.tvCustomerPhone.setOnClickListener {
            val phone = b.tvCustomerPhone.text.toString()
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            startActivity(intent)
        }

        b.btnDelivered.setOnClickListener { updateOrderStatus(OrderStatus.ENTREGADO) }
        b.btnPending.setOnClickListener { updateOrderStatus(OrderStatus.PENDIENTE) }
        b.btnInTransit.setOnClickListener { updateOrderStatus(OrderStatus.EN_REPARTO) }
    }

    private fun openMap(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun updateOrderStatus(status: OrderStatus) {
        val newData = hashMapOf<String, Any>(
            "status" to status.code,
        )
        if (status == OrderStatus.ENTREGADO)
            newData["delivered_date"] = FieldValue.serverTimestamp()
        if (status == OrderStatus.EN_REPARTO)
            newData["delivering_date"] = FieldValue.serverTimestamp()
        if (status == OrderStatus.PENDIENTE)
            newData["delivering_date"] = FieldValue.delete()

        db.collection("orders").document(orderId).update(newData).addOnCompleteListener {
            if (it.isSuccessful) {
                if (status == OrderStatus.ENTREGADO)
                    finish()
                Toast.makeText(
                    this@OrderInfoActivity,
                    "¡Estado Actualizado con exito!",
                    Toast.LENGTH_SHORT
                ).show()
                getOrderInfo(orderId)
            }
        }
    }

    fun formatDate(timestamp: Timestamp?): String {
        timestamp ?: return "SIN FECHA"

        val date = timestamp.toDate()
        val format = SimpleDateFormat("EEEE, d MMMM yyyy hh:mm a", Locale("es", "ES"))
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    }


    private fun getOrderInfo(orderId: String) {
        db.collection("orders").document(orderId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val doc = it.result
                b.tvOrderId.text = doc.id
                b.tvCustomerName.text = doc.getString("customerName")
                b.tvOrderDetails.text = doc.getString("orderDetails")
                b.tvCustomerPhone.text = doc.getString("customerPhone")
                b.tvCustomerAddress.text = doc.getString("customerAddress")
                b.tvDeliveringLocation.text = doc.getString("delivering_location") ?: NO_LOCATION
                val status = try {
                    doc.getLong("status") ?: 1L
                } catch (e: Exception) {
                    1L
                }

                b.tvStatus.text = OrderStatus.fromInt(status).name
                if (status != OrderStatus.ENTREGADO.code) {
                    b.btnPending.visibility = View.VISIBLE
                    b.btnDelivered.visibility = View.VISIBLE
                    b.btnInTransit.visibility = View.VISIBLE
                }

                b.tvDate.text = formatDate(doc.getTimestamp("date"))
                b.tvLocation.text = doc.getString("location") ?: NO_LOCATION
                b.tvDeliveredDate.text = formatDate(doc.getTimestamp("delivered_date"))
                b.tvDeliveringDate.text = formatDate(doc.getTimestamp("delivering_date"))
            }
        }
    }
}