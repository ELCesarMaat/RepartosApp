package com.robles.cesar.pedidosapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.robles.cesar.pedidosapp.databinding.ActivityMenuBinding
import com.robles.cesar.pedidosapp.NuevoPedidoActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMenu.toolbar)

        binding.appBarMenu.fab.setOnClickListener {
            val i = Intent(applicationContext, NuevoPedidoActivity::class.java)
            startActivity(i)
        }

        binding.appBarMenu.fab.visibility = View.GONE
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_deliveries, R.id.nav_orders
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        getCurrentUser()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_menu_logout -> {
                auth.signOut()
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getCurrentUser() {
        val userId = auth.currentUser?.uid ?: ""
        db.collection("deliveries").document(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, it.result.get("name").toString(), Toast.LENGTH_SHORT).show()
                val doc = it.result
                if (doc.getBoolean("admin") == true) {
                    showNavItem(R.id.nav_deliveries, true)
                    showNavItem(R.id.nav_my_orders, false)
                    binding.appBarMenu.fab.visibility = View.VISIBLE
                }
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.txt_name_header)
                    .text = doc.getString("name")
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.txt_email_header)
                    .text = doc.getString("email")
            }
        }
    }

    private fun showNavItem(itemId: Int, show: Boolean) {
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        val item = menu.findItem(itemId)
        item.isVisible = show
    }


}