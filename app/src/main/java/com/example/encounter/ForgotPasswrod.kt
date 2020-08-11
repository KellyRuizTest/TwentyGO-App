package com.example.encounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.transition.Slide
import com.example.encounter.Model.Users
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_forgot_passwrod.*
import kotlinx.android.synthetic.main.activity_registartion.*

class ForgotPasswrod : AppCompatActivity() {

    val fAuth : FirebaseAuth? = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_passwrod)

        send_email_btn.setOnClickListener { sendEmailtoVerify() }

    }

    private fun sendEmailtoVerify() {

        if (!validateEmail()){

            val snackbar: Snackbar = Snackbar.make(send_email, "please put a correct email to verify", Snackbar.LENGTH_SHORT)
            snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            snackbar.anchorView= send_email_btn
            snackbar.show()
        }else {

            val emailtoOTP = send_email.text.toString()
            if (!validateExists(emailtoOTP)) {

                fAuth?.sendPasswordResetEmail(emailtoOTP)?.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Link to reset password was sent to your email",
                            Toast.LENGTH_LONG
                        ).show()
                        val transition: Slide = Slide(Gravity.END)
                        transition.duration = 2000
                        window.exitTransition

                        startActivity(
                            Intent(applicationContext, LoginActivity::class.java),
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error! reset link was not sent",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else{
                textfield_email_send.error = "This email does not exists!"
            }
        }
    }

    private fun validateExists(aux : String) : Boolean {

        val email: String = send_email.text.toString()
        var descision : Boolean = false

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users")

        userInfo.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                for (snapshot in p0.children){
                    val user = snapshot.getValue(Users::class.java)
                    if (user?.getEmail() == aux){
                        descision = true

                        println("What is your descision?"+ descision)

                    }
                }
            }
        })

        return descision
    }

    private fun validateEmail() : Boolean {
        val email: String = send_email.text.toString()
        val checkemail = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

        if (email.isEmpty()) {
            return false
        } else if (!email.matches(checkemail)) {
            return false
        } else {
            send_email.error = null
            return true
        }

    }


}