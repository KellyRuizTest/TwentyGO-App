package com.twenty.veinte.Model

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.twenty.veinte.Adapters.UserAdapter
import com.twenty.veinte.MainActivity
import com.twenty.veinte.R
import kotlinx.android.synthetic.main.activity_suggestion.*

class SuggestionActivity : AppCompatActivity() {

    private var recyclerView : RecyclerView? = null
    private var userAdapter : UserAdapter? = null
    private var listUser : List<Users>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion)

        next_button.setOnClickListener {
            val intentToMain = Intent(this, MainActivity::class.java)
            intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentToMain)
            finish()
        }

        recyclerView = findViewById(R.id.firs_user_toshow)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        listUser = ArrayList()
        userAdapter = UserAdapter(this, listUser as ArrayList<Users>, false)
        recyclerView?.adapter = userAdapter

        showUser()

    }

    private fun showUser() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (listUser as ArrayList<Users>).clear()

                for (snapshoy in dataSnapshot.children) {
                    val user = snapshoy.getValue(Users::class.java)
                    (listUser as ArrayList<Users>).add(user!!)
                }
                userAdapter?.notifyDataSetChanged()

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}