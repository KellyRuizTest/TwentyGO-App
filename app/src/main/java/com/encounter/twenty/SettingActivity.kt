package com.encounter.twenty

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.encounter.twenty.Model.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private lateinit var  firebaseUser : FirebaseUser
    private var photoCheck: Boolean = false
    private var URL = ""
    private var imageUri: Uri? = null
    private var storageProfileRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfileRef = FirebaseStorage.getInstance().reference.child("Profile Picture")

        circle_change_imageprofile.setOnClickListener {
            photoCheck = true
            CropImage.activity().setAspectRatio(1,1).start(this@SettingActivity)
        }

        register_account.setOnClickListener {
            if (photoCheck) {
                uploadImage()
            } else {
                saveUserData()
            }
        }

        userCompleteInfo()
    }

    private fun userCompleteInfo() {

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser?.uid.toString())
        userInfo.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    val user = p0.getValue<Users>(Users::class.java)

                    edit_name.setText(user!!.getName())
                    edit_id.setText(user!!.getUsername())
                    edit_bio.setText(user!!.getBio())
                    edit_email.setText(user!!.getEmail())
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale)
                        .into(circle_change_imageprofile)

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
                Toast.makeText(
                    this,
                    "To be more elegant write something in your Bio",
                    Toast.LENGTH_SHORT
                ).show()
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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            circle_change_imageprofile.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {

        val dialogProgress = ProgressDialog(this)
        dialogProgress.setTitle("Uploading Image")
        dialogProgress.setMessage("Please wait a minute")
        dialogProgress.show()

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

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Updating Profile picture")
                progressDialog.setMessage("Please wait")
                progressDialog.show()

                val fileRef = storageProfileRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (task.isSuccessful) {

                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener { task ->

                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        URL = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>()
                        userMap["name"] = edit_name.text.toString()
                        userMap["username"] = edit_id.text.toString()
                        userMap["bio"] = edit_bio.text.toString()
                        userMap["image"] = URL

                        ref.child(firebaseUser!!.uid).updateChildren(userMap)
                        val intentToMain = Intent(this, MainActivity::class.java)
                        startActivity(intentToMain)
                        finish()
                        progressDialog.dismiss()
                    }else{
                        Toast.makeText(this, "Another Error", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()

                    }
                })

            }

        }
    }
}