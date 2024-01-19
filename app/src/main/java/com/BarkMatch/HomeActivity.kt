package com.BarkMatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navHostFragment: NavHostFragment? = supportFragmentManager.findFragmentById(R.id.navHostHome) as? NavHostFragment
        navController = navHostFragment?.navController
//        navController?.let { NavigationUI.setupActionBarWithNavController(this, it) }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.homeActivityBottomNavigationView)
        navController?.let { NavigationUI.setupWithNavController(bottomNavigationView, it) }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.ProfilePostFragment) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController?.navigateUp()
                true
            }
            else -> navController?.let { NavigationUI.onNavDestinationSelected(item, it) } ?: super.onOptionsItemSelected(item)
        }
    }
}