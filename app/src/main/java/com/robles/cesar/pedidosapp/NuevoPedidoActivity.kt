package com.robles.cesar.pedidosapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.cesar.pedidosapp.databinding.ActivityNuevoPedidoBinding
import com.robles.cesar.pedidosapp.models.Delivery
import com.robles.cesar.pedidosapp.models.OrderStatus
import io.grpc.Server
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.Date

class NuevoPedidoActivity : AppCompatActivity() {
    var delivery: Delivery? = null
    private val deliveriesList = arrayListOf<Delivery>()
    private lateinit var loadingDialog: AlertDialog
    private val db = FirebaseFirestore.getInstance()
    lateinit var b: ActivityNuevoPedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityNuevoPedidoBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnEnviarPedido.setOnClickListener {
            if (validateFields())
                showConfirmDialog(b.txtUbicacion.editText?.text.toString())
        }

        b.btnSeleccionarRepartidor.setOnClickListener {
            showSelectDelivery()
        }

        invalidateFields()
        getDeliveries()
    }

    private fun validateFields(): Boolean {
        var result = true

        if ((b.txtNombreCliente.editText?.text?.length ?: 0) < 3) {
            b.txtNombreCliente.isErrorEnabled = true
            b.txtNombreCliente.error = "Ingrese un nombre valido"
            result = false
        }

        if (!b.txtCelular.editText?.text.toString().matches("^[+]?[0-9]{7,13}$".toRegex())) {
            b.txtCelular.isErrorEnabled = true
            b.txtCelular.error = "Numero de telefono no valido"
            result = false
        }

        if ((b.txtDireccion.editText?.text?.length ?: 0) < 5) {
            b.txtDireccion.isErrorEnabled = true;
            b.txtDireccion.error = "Direccion no valida"
            result = false
        }

        if ((b.txtDetalles.editText?.text?.length ?: 0) < 5) {
            b.txtDetalles.isErrorEnabled = true
            b.txtDetalles.error = "Ingrese los detalles"
            result = false
        }

        if ((b.txtUbicacion.editText?.text?.length ?: 0) < 5) {
            b.txtUbicacion.isErrorEnabled = true
            b.txtUbicacion.error = "Ingrese un URL Valido"
            result = false
        }

        if (delivery == null) {
            Toast.makeText(this, "Seleccione un repartidor", Toast.LENGTH_SHORT).show()
            result = false
        }

        return result
    }

    private fun invalidateFields() {
        b.txtNombreCliente.editText?.addTextChangedListener {
            b.txtNombreCliente.isErrorEnabled = false
        }
        b.txtCelular.editText?.addTextChangedListener { b.txtCelular.isErrorEnabled = false }
        b.txtDetalles.editText?.addTextChangedListener { b.txtDetalles.isErrorEnabled = false }
        b.txtDireccion.editText?.addTextChangedListener { b.txtDireccion.isErrorEnabled = false }
        b.txtUbicacion.editText?.addTextChangedListener { b.txtUbicacion.isErrorEnabled = false }
    }

    private fun saveOrder() {
        val customerName = b.txtNombreCliente.editText?.text.toString()
        val customerPhone = b.txtCelular.editText?.text.toString()
        val customerAddress = b.txtDireccion.editText?.text.toString()
        val orderDetails = b.txtDetalles.editText?.text.toString()
        val location = b.txtUbicacion.editText?.text.toString()
        val delivering_location = b.txtRecogerEn.editText?.text.toString()
        val deliveryId = delivery?.id
        val orderData = hashMapOf(
            "date" to FieldValue.serverTimestamp(),
            "customerName" to customerName,
            "customerPhone" to customerPhone,
            "customerAddress" to customerAddress,
            "orderDetails" to orderDetails,
            "deliveryId" to deliveryId,
            "delivering_location" to delivering_location,
            "location" to location,
            "status" to OrderStatus.PENDIENTE.code
        )
        db.collection("orders").add(orderData).addOnCompleteListener {
            hideLoadingDialog()
            if (it.isSuccessful) {
                Toast.makeText(this, "Orden agregada con exito", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmDialog(url: String) {
        val dialogo = AlertDialog.Builder(this)
        dialogo.setTitle("Confirmar Pedido")
        dialogo.setMessage("¿Estás seguro de que quieres enviar este pedido?")

        dialogo.setPositiveButton("Confirmar") { _, _ ->
            showLoadingDialog()
            resolveUrl(url)
        }

        dialogo.setNegativeButton("Cancelar") { _, _ ->
        }
        dialogo.create().show()
    }

    private fun resolveUrl(url: String) {
        val client = OkHttpClient()

        try {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    hideLoadingDialog()
                    b.txtDireccion.isErrorEnabled = true
                    b.txtDireccion.error = "El URL No se pudo cargar"
                }

                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread { b.txtUbicacion.editText?.setText(url) }
                    saveOrder()
                }
            })
        }
        catch (e: Exception){
            hideLoadingDialog()
            b.txtUbicacion.isErrorEnabled = true
            b.txtUbicacion.error = "URL No valido"
        }
    }

    private fun showLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(false) // Para evitar que el usuario cierre el diálogo

        loadingDialog = builder.create()
        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun getDeliveries() {
        db.collection("deliveries").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (doc in it.result.documents) {
                    val id = doc.id;
                    val name = doc.get("name").toString()
                    val phone = doc.get("phone").toString()
                    val email = doc.get("email").toString()
                    val admin = doc.getBoolean("admin") ?: false
                    if(admin)
                        continue
                    deliveriesList.add(Delivery(id, name, phone, email, admin))
                }

            }
        }
    }

    private fun showSelectDelivery() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona un Repartidor")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            deliveriesList.map { "${it.name}/${it.phone}" })
        builder.setAdapter(adapter) { dialog, which ->
            delivery = deliveriesList[which]
            b.txtInfoRepartidor.text = "${delivery?.name}\n${delivery?.phone}"
        }
        val dialog = builder.create()
        dialog.show()
    }


}