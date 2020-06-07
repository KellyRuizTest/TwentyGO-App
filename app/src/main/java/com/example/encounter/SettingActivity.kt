package com.example.encounter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.encounter.Model.Users
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var photoCheck : Boolean = false
    private var UrL = ""
    private var imageUri : Uri? = null
    private var storageReference : StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        storageReference = FirebaseStorage.getInstance().reference.child("Profile Picture")

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intentToMain = Intent(this, LoginActivity::class.java)
            intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentToMain)
            finish()
        }

        circle_change_imageprofile.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            photoCheck = true

            startActivityForResult(intent,1)
            //CropImage.activity().setAspectRatio(1,1).start(this@SettingActivity)
        }

        register_account.setOnClickListener {
            if (photoCheck){
                uploadImage()
            } else {
                saveUserData()
            }
        }

        userCompleteInfo()
    }

    private fun userCompleteInfo(){

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser?.uid.toString())
        userInfo.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<Users>(Users::class.java)

                    edit_name.setText(user!!.getName())
                    edit_id.setText(user!!.getUsername())
                    edit_bio.setText(user!!.getBio())
                    edit_email.setText(user!!.getEmail())
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale).into(circle_change_imageprofile)

                }
            }
        })
    }

    private fun saveUserData() {

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users")

        when {
            TextUtils.isEmpty(edit_name.text.toString()) -> {
                Toast.makeText(this, "Put anything in the Name", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edit_id.text.toString()) -> {
                Toast.makeText(this, "Put anything in your Username", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edit_bio.text.toString()) -> {
                Toast.makeText(this, "To be more elegant write something in your Bio", Toast.LENGTH_SHORT).show()
            }
            else -> {

                val userMap = HashMap<String, Any>()
                userMap["name"] = edit_name.text.toString()
                userMap["username"] = edit_id.text.toString()
                userMap["bio"] = edit_bio.text.toString()

                userInfo.child(firebaseUser!!.uid).updateChildren(userMap)
                Toast.makeText(this, "Account Information was updated", Toast.LENGTH_SHORT).show()

                val intentToMain = Intent(this, MainActivity::class.java)
                startActivity(intentToMain)
                finish()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode== Activity.RESULT_OK && data!= null){
            //val result = CropImage.getActivityResult(data)
            imageUri = data.data
            Picasso.get().load(imageUri).placeholder(R.drawable.usermale).into(circle_change_imageprofile)
        }else{

            Toast.makeText(this, "There is a issue", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadImage() {

        val dialogProgres = ProgressDialog(this)
        dialogProgres.setTitle("Uploading Image")
        dialogProgres.setMessage("Please wait a minute")
        dialogProgres.show()

        when {

            imageUri == null -> Toast.makeText(
                this,
                "Please select image first",
                Toast.LENGTH_SHORT
            ).show()

            TextUtils.isEmpty(edit_name.text.toString()) -> {
                Toast.makeText(this, "Put anything in the Name", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edit_id.text.toString()) -> {
                Toast.makeText(this, "Put anything in your Username", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edit_bio.text.toString()) -> {
                Toast.makeText(
                    this,
                    "To be more elegant write something in your Bio",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val fileRed = storageReference!!.child(firebaseUser!!.uid + ".jpg")
                val uploadTask: StorageTask<*>
                uploadTask = fileRed.putFile(imageUri!!)

                val urlTask = uploadTask.continueWith { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            dialogProgres.dismiss()
                        }
                    }
                    return@continueWith fileRed.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val downloadUri = task.result
                        UrL = downloadUri.toString()
                        println("=======================================================================================")
                        println(UrL)
                        println(fileRed.toString())
                        println("=======================================================================================")
                        val ref = FirebaseDatabase.getInstance().reference.child("users")
                        val userMap = HashMap<String, Any>()
                        userMap["name"] = edit_name.text.toString()
                        userMap["username"] = edit_id.text.toString()
                        userMap["bio"] = edit_bio.text.toString()
                        userMap["image"] = UrL

                        ref.child(firebaseUser!!.uid).updateChildren(userMap)
                        val intentToMain = Intent(this, MainActivity::class.java)
                        startActivity(intentToMain)
                        finish()
                        dialogProgres.dismiss()
                    } else{
                        dialogProgres.dismiss()
                    }

                }
            }
        }
    }

}