package com.robles.cesar.pedidosapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.robles.cesar.pedidosapp.databinding.ActivitySpashScreenBinding

class SpashScreen : AppCompatActivity() {
    private lateinit var b : ActivitySpashScreenBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySpashScreenBinding.inflate(layoutInflater)
        setContentView(b.root)

        val user = auth.currentUser

        if(user != null){
            showMainScreen()
        }
        else {
            showLoginScreen()
        }
    }

    private fun showMainScreen(){
        val i  = Intent(this, MenuActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun showLoginScreen(){
        val i  = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}