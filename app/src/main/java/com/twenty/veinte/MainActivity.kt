package com.twenty.veinte

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.twenty.veinte.fragment.HomeFragment
import com.twenty.veinte.fragment.NotifyFragment
import com.twenty.veinte.fragment.PerfilFragment
import com.twenty.veinte.fragment.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kotlinpermissions.KotlinPermissions

class MainActivity : AppCompatActivity() {

    internal var framgent_used : Fragment? = null
    val read_storage_agree = 1
    val write_storage_agree = 1
    val camera_agree = 1

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
               FragmentFocused(HomeFragment())
            }
            R.id.navigation_search -> {
                FragmentFocused(SearchFragment())
            }
            R.id.navigation_add -> {

                sendToPostActivity()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_notification -> {
                FragmentFocused(NotifyFragment())
            }
            R.id.navigation_profile -> {
                FragmentFocused(PerfilFragment())
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView : BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        FragmentFocused(HomeFragment())

    }

    private fun FragmentFocused(fragment: Fragment){
        val fragmentTrans = supportFragmentManager.beginTransaction().addToBackStack(null)
        fragmentTrans.replace(R.id.content_fragment, fragment).commit()
    }

    private fun sendToPostActivity(Param : String){
        val intentToPost = Intent(this, PostActivity::class.java)
        intentToPost.putExtra("PERMISSIONS_GRANTED", Param)
        startActivity(intentToPost)
        finish()
    }

    private fun sendToPostActivity(){
        val intentToPost = Intent(this, PostActivity::class.java)
        startActivity(intentToPost)
        finish()
    }

}