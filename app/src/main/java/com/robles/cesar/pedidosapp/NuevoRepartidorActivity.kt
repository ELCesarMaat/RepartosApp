package com.robles.cesar.pedidosapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.cesar.pedidosapp.databinding.ActivityNuevoRepartidorBinding
import java.util.regex.Pattern

class NuevoRepartidorActivity : AppCompatActivity() {
    private lateinit var loadingDialog: AlertDialog

    private lateinit var b: ActivityNuevoRepartidorBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityNuevoRepartidorBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnGuardarRepartidor.setOnClickListener {
            if (validateFields()) {
                b.btnGuardarRepartidor.isEnabled = false
                showConfirmDialog()
            }
        }

        invalidateFields()

    }

    private fun saveDelivery() {
        val name = b.txtNombre.editText?.text.toString()
        val phone = b.txtCelular.editText?.text.toString()
        val pin = b.txtPin.editText?.text.toString()
        val email = b.txtCorreo.editText?.text.toString()

        val orderData = hashMapOf(
            "name" to name,
            "phone" to phone,
            "email" to email,
            "admin" to false
        )

        auth.createUserWithEmailAndPassword(email, pin).addOnCompleteListener {
            if (it.isSuccessful) {
                db.collection("deliveries").document(auth.currentUser?.uid!!).set(orderData).addOnCompleteListener { task ->
                    hideLoadingDialog()
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Repartidor registrado con exito", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "Ocurrio un error compruebe su conexion a internet",
                            Toast.LENGTH_LONG
                        ).show()
                        b.btnGuardarRepartidor.isEnabled = true
                    }
                }
            }
            else{
                hideLoadingDialog()
                Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                b.btnGuardarRepartidor.isEnabled = true
            }
        }
    }

    private fun validateFields(): Boolean {
        var res = true

        if ((b.txtNombre.editText?.text?.length ?: 0) < 3) {
            b.txtNombre.isErrorEnabled = true
            b.txtNombre.error = "Ingrese un nombre valido"
            res = false
        }
        if (!b.txtCelular.editText?.text.toString().matches("^[+]?[0-9]{8,13}$".toRegex())) {
            b.txtCelular.isErrorEnabled = true
            b.txtCelular.error = "Ingrese un telefono valido"
            res = false
        }

        if ((b.txtPin.editText?.text?.length ?: 0) < 4) {
            b.txtPin.isErrorEnabled = true
            b.txtPin.error = "Ingrese un pin de al menos 4 digitos"
            res = false
        }

        if (b.txtRepetirPin.editText?.text.toString() != b.txtPin.editText?.text.toString()) {
            b.txtRepetirPin.isErrorEnabled = true
            b.txtRepetirPin.error = "Los pines no coinciden"
            res = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(b.txtCorreo.editText?.text.toString()).matches()) {
            b.txtCorreo.isErrorEnabled = true
            b.txtCorreo.error = "Ingrese un correo valido"
            res = false
        }

        return res;
    }

    private fun invalidateFields() {
        b.txtNombre.editText?.addTextChangedListener { b.txtNombre.isErrorEnabled = false }
        b.txtCelular.editText?.addTextChangedListener { b.txtCelular.isErrorEnabled = false }
        b.txtPin.editText?.addTextChangedListener { b.txtPin.isErrorEnabled = false }
        b.txtRepetirPin.editText?.addTextChangedListener { b.txtRepetirPin.isErrorEnabled = false }
        b.txtCorreo.editText?.addTextChangedListener { b.txtCorreo.isErrorEnabled = false }
    }

    private fun checkUserExists(phone: String, callback: (exists: Boolean) -> Unit) {
        db.collection("deliveries").whereEqualTo("phone", phone).get().addOnSuccessListener {
            callback(!it.isEmpty)
        }.addOnFailureListener {
            callback(true)
        }
    }

    private fun showConfirmDialog() {
        val dialogo = AlertDialog.Builder(this)
        dialogo.setTitle("Confirmar")
        dialogo.setMessage("¿Estás seguro de que quieres registrar este repartidor?")

        dialogo.setPositiveButton("Confirmar") { _, _ ->
            checkUserExists(b.txtCelular.editText?.text.toString()) {
                if (it) {
                    Toast.makeText(this, "El numero ya se encuentra registrado", Toast.LENGTH_SHORT)
                        .show()
                    b.btnGuardarRepartidor.isEnabled = true
                } else {
                    showLoadingDialog()
                    saveDelivery()
                }
            }

        }

        dialogo.setNegativeButton("Cancelar") { _, _ ->
        }
        dialogo.create().show()
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


}