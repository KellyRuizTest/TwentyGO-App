package com.example.encounter

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registartion.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registartion)

        register_account_btn.setOnClickListener {
            createAccount()
        }

    }

    private fun createAccount() {

        val name = edit_name.text.toString()
        val email = edit_email.text.toString()
        val username = edit_id.text.toString()
        val password = edit_pass.text.toString()

        when {
            TextUtils.isEmpty(name) -> Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()

            else -> {
                val userAuth : FirebaseAuth = FirebaseAuth.getInstance()
                val progressTask = ProgressDialog(this)
                progressTask.setTitle("Registration")
                progressTask.setMessage("Please wait a second")
                progressTask.setCanceledOnTouchOutside(false)
                progressTask.show()
                userAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->

                    if(task.isSuccessful){
                        saveInfo(name, username, email, password, progressTask)
                    }else{
                        Toast.makeText(this, "Error ", Toast.LENGTH_SHORT).show()
                        userAuth.signOut()
                        progressTask.dismiss()
                    }
                }
            }
        }
    }

    private fun saveInfo(varName : String, varUsername: String, varEmail : String, varPW : String, varProgress : ProgressDialog){

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["pid"] = userID
        userMap["name"] = varName
        userMap["username"] = varUsername
        userMap["email"] = varEmail
        userMap["password"] = varPW
        userMap["bio"] = "I'm here to find my way"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/encounter-app-90614.appspot.com/o/forbidden%20image%2Fusermale.png?alt=media&token=71bc06c8-bc28-46b1-98f0-277011d43886"

        userRef.child(userID).setValue(userMap).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                varProgress.dismiss()
                Toast.makeText(this, "Account was created successfully", Toast.LENGTH_SHORT).show()

                FirebaseDatabase.getInstance().reference.child("Follow").child(userID).child("following").child(userID).setValue(true)

                val intentToMain = Intent(this, MainActivity::class.java)
                intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentToMain)
                finish()
            }else{

                println("Error just to creating account")
            }
        }
    }

}