package com.marknkamau.justjava.ui

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.marknkamau.justjava.JustJavaApp
import com.marknkamau.justjava.R
import com.marknkamau.justjava.ui.about.AboutActivity
import com.marknkamau.justjava.ui.cart.CartActivity
import com.marknkamau.justjava.ui.checkout.CheckoutActivity
import com.marknkamau.justjava.ui.login.LogInActivity
import com.marknkamau.justjava.ui.profile.ProfileActivity

abstract class BaseActivity : AppCompatActivity() {

    private val authService by lazy { (application as JustJavaApp).authService }
    private val preferencesRepository by lazy { (application as JustJavaApp).preferencesRepo }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (this is CartActivity || this is CheckoutActivity) {
            menu?.findItem(R.id.menu_cart)?.isVisible = false
        }
        if (this is ProfileActivity) {
            menu?.findItem(R.id.menu_profile)?.isVisible = false
        }

        if (authService.isSignedIn()) {
            menu?.findItem(R.id.menu_login)?.isVisible = false
//            menu?.findItem(R.id.menu_profile)?.isVisible = true
            menu?.findItem(R.id.menu_logout)?.isVisible = true
        } else {
            menu?.findItem(R.id.menu_login)?.isVisible = true
//            menu?.findItem(R.id.menu_profile)?.isVisible = false
            menu?.findItem(R.id.menu_logout)?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_cart -> {
                startActivity(Intent(this, CartActivity::class.java))
                return true
            }
            R.id.menu_login -> {
                startActivity(Intent(this, LogInActivity::class.java))
                return true
            }
            R.id.menu_profile -> {
                if (authService.isSignedIn()) {
                    startActivity(Intent(this, ProfileActivity::class.java))
                } else {
                    startActivity(Intent(this, LogInActivity::class.java))
                }
                return true
            }
            R.id.menu_logout -> {
                invalidateOptionsMenu()
                preferencesRepository.clearUserDetails()
                authService.logOut()
                // If this is ProfileActivity
                (this as? ProfileActivity)?.finish()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.menu_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
