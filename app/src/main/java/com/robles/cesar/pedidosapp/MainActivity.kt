package com.robles.cesar.pedidosapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.cesar.pedidosapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            val email = b.txtEmail.editText?.text.toString()
            val password = b.txtPassword.editText?.text.toString()
            if (validateFields())
                login(email, password)
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
        return res;
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = auth.currentUser?.uid
                Toast.makeText(this, "User ID: $userId", Toast.LENGTH_SHORT).show()
                showMainScreen()
            } else {
                handleLoginError(it.exception)
            }

        }
    }

    fun handleLoginError(exception: Exception?) {
        exception?.let {
            when (it) {
                is FirebaseAuthInvalidUserException -> {
                    // El usuario no existe o fue deshabilitado
                    Toast.makeText(this@MainActivity, "Usuario no encontrado", Toast.LENGTH_SHORT)
                        .show()
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    // La contraseña es incorrecta o el formato del correo electrónico es inválido
                    Toast.makeText(this@MainActivity, "Credenciales inválidas", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    // Otros errores
                    Toast.makeText(
                        this@MainActivity,
                        "Error en el inicio de sesión: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showMainScreen() {
        val i = Intent(this, MenuActivity::class.java)
        startActivity(i);
        finish()
    }
}