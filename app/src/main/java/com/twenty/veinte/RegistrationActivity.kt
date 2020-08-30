package com.twenty.veinte

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.util.Linkify
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registartion.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_registartion)


        val builder = MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
        val tittle = SpannableString("Terms and Data Policy")
        tittle.setSpan(ForegroundColorSpan(Color.parseColor("#121212")),0, tittle.length, 0)
        tittle.setSpan(StyleSpan(Typeface.BOLD),0,tittle.length,0)
        tittle.setSpan(TypefaceSpan("sans-serif-light"),0,tittle.length,0)


        val s = SpannableString("https://drive.google.com/file/d/1d0S1Pp-vC7Xsof35o6YHOWJJHqnFluXR/view?usp=sharing")
        Linkify.addLinks(s, Linkify.WEB_URLS)
        val textView = TextView(applicationContext)
        textView.text = s
        textView.movementMethod = LinkMovementMethod.getInstance()


        builder.setTitle(tittle)
        builder.setMessage("By clicking continue, you agree to our Terms and Privacy Policy ")
        builder.setView(textView)
       // builder.setMessage("and Privacy Policy")
        builder.setPositiveButton("Continue", null)
        val dialogInterface = builder.create()
        dialogInterface.show()


        register_account_btn.setOnClickListener {
            createAccountWithValidations()
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

    private fun createAccountWithValidations() {

        val name = edit_name.text.toString()
        val email = edit_email.text.toString()
        val username = edit_id.text.toString()
        val password = edit_pass.text.toString()

        if (!validateName() || !validateEmail() || !validateUsername() || !validatePassword()) {

            return
        }

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

    private fun validateName(): Boolean {
        val name = edit_name.text.toString()

        if(name.isEmpty()){
            edit_name.error = "Field can not be empty"
            return false
        } else {
            edit_name.error = null
            return true
        }

    }

    private fun validateUsername() : Boolean {
        val username: String = edit_id.text.toString()
        val checkspaces = Regex("\\A\\w{4,20}\\z")

        if (username.isEmpty()) {
            edit_id.error = "Field can not be empty"
            return false
        } else if (username.length > 20) {
            edit_id.error = "Username field is too large"
            return false
        } else if (!username.matches(checkspaces)) {
            edit_id.error = "No white spaces are allowed or special character are allowed"
            return false
        } else {
            edit_id.error = null
            return true
        }

    }

    private fun validateEmail() : Boolean {
        val email: String = edit_email .text.toString()
        val checkemail = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

        if (email.isEmpty()) {
            edit_email.error = "Field can not be empty"
            return false
        } else if (!email.matches(checkemail)) {
            edit_email.error = "Invalid email address!"
            return false
        } else {
            edit_email.error = null
            return true
        }

    }

    private fun validatePassword() : Boolean {
        val passwrd: String = edit_pass .text.toString()
        val checkPasswrd = Regex("^" + "(?=.*[a-zA-Z])" + "(?=\\S+$)" + ".{4,10}" + "$")

        if (passwrd.isEmpty()) {
            edit_pass.error = "Field can not be empty"
            return false
        } else if (!passwrd.matches(checkPasswrd)) {
            edit_pass.error = "must contain at least 4 characters, at least 1 letter, no spaces"
            return false
        } else {
            edit_pass.error = null
            return true
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
        userMap["bio"] = "Welcome to my bio!"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/twenty-app-90614.appspot.com/o/forbidden%20image%2Fusermale.png?alt=media&token=71bc06c8-bc28-46b1-98f0-277011d43886"

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
