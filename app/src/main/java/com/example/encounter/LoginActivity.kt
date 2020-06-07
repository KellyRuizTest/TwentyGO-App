package com.example.encounter

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registration_button.setOnClickListener {
            startActivity(Intent(Intent(this, RegistrationActivity::class.java))) }

        login_account.setOnClickListener {LoginUser() }
    }

    private fun LoginUser(){

        val email = edit_email_login.text.toString()
        val password = edit_pass_login.text.toString()

        when {

            TextUtils.isEmpty(email) -> Toast.makeText(this, "Username/Email is required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()

            else -> {
                val progressTask = ProgressDialog(this)
                progressTask.setTitle("Login")
                progressTask.setMessage("Please wait a second")
                progressTask.setCanceledOnTouchOutside(false)
                progressTask.show()

                val userAuth : FirebaseAuth = FirebaseAuth.getInstance()
                userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        progressTask.dismiss()
                        Toast.makeText(this, "Logged successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressTask.dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser !=null){

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }

    }
}