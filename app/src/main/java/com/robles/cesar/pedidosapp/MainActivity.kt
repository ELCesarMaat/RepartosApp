package com.robles.cesar.pedidosapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.cesar.pedidosapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            val email = b.txtEmail.editText?.text.toString()
            val password = b.txtPassword.editText?.text.toString()
            if (validateFields()) {
                b.txtError.visibility = View.GONE
                b.btnLogin.isEnabled = false
                login(email, password)
            }
        }
        invalidateFields()
    }

    private fun invalidateFields() {
        b.txtEmail.editText?.addTextChangedListener { b.txtEmail.isErrorEnabled = false }
        b.txtPassword.editText?.addTextChangedListener { b.txtPassword.isErrorEnabled = false }
    }

    private fun validateFields(): Boolean {
        var res = true
        if (!Patterns.EMAIL_ADDRESS.matcher(b.txtEmail.editText?.text ?: "").matches()) {
            b.txtEmail.isErrorEnabled = true
            b.txtEmail.error = "Ingrese un email valido"
            res = false
        }
        if (b.txtPassword.editText?.text.toString().isEmpty()) {
            b.txtPassword.isErrorEnabled = true
            b.txtPassword.error = "Ingrese su contraseña"
            res = false
        }
        return res
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = auth.currentUser?.uid
                Toast.makeText(this, "User ID: $userId", Toast.LENGTH_SHORT).show()
                showMainScreen()
            } else {
                b.btnLogin.isEnabled = true
                handleLoginError(it.exception)
            }

        }
    }

    fun handleLoginError(exception: Exception?) {
        var error_message = ""
        exception?.let {
            error_message = when (it) {
                is FirebaseAuthInvalidUserException -> {
                    "Usuario no encontrado"
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    // La contraseña es incorrecta o el formato del correo electrónico es inválido
                    "Credenciales inválidas"
                }

                else -> {
                    it.message.toString()
                }
            }
        }
        b.txtError.text = error_message
        b.txtError.visibility = View.VISIBLE
    }

    private fun showMainScreen() {
        val i = Intent(this, MenuActivity::class.java)
        startActivity(i);
        finish()
    }
}