package com.twenty.dec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.twenty.dec.fragment.HomeFragment
import com.twenty.dec.fragment.NotifyFragment
import com.twenty.dec.fragment.PerfilFragment
import com.twenty.dec.fragment.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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