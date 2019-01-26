package com.marknkamau.justjavastaff.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.marknjunge.core.auth.AuthService
import com.marknkamau.justjavastaff.JustJavaStaffApp

import com.marknkamau.justjavastaff.R
import com.marknkamau.justjavastaff.ui.help.HelpActivity
import com.marknkamau.justjavastaff.ui.login.LogInActivity
import com.marknkamau.justjavastaff.ui.preferences.SettingsActivity

/**
 * Created by Mark Njung'e.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private lateinit var auth: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = (application as JustJavaStaffApp).auth
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_log_out ->{
                auth.logOut()
                val intent = Intent(this, LogInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
                true
            }
            R.id.menu_settings ->{
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.menu_help ->{
                startActivity(Intent(this, HelpActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
