package com.twenty.veinte

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.twenty.veinte.fragment.HomeFragment
import com.twenty.veinte.fragment.NotifyFragment
import com.twenty.veinte.fragment.PerfilFragment
import com.twenty.veinte.fragment.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    internal var framgent_used : Fragment? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
               FragmentFocused(HomeFragment())
            }
            R.id.navigation_search -> {
                FragmentFocused(SearchFragment())
            }
            R.id.navigation_add -> {
                startActivity(Intent(this, PostActivity::class.java))
                return@OnNavigationItemSelectedListener true }

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



}