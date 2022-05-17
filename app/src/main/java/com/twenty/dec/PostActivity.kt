package com.twenty.dec

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.twenty.dec.Model.Users
import com.twenty.dec.fragment.TimePickerFragment
import kotlinx.android.synthetic.main.activity_post.*


class PostActivity : AppCompatActivity() {

    private var URL = ""
    private var checkif = false
    private var imageUri : Uri? = null
    private var storageProfileRef : StorageReference? = null
    private lateinit var  firebaseUser : FirebaseUser

    private var READ_STORAGE_AGREE = 1
    private var WRITE_STORAGE_AGREE =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContentView(R.layout.activity_post)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfileRef = FirebaseStorage.getInstance().reference.child("Post Pictures")

        button_postingclick.setOnClickListener {
            uploadImage()
        }

        //CropImage.activity().setAspectRatio(1,1).start(this)

        back_intent.setOnClickListener { val intentToMain = Intent(this, MainActivity::class.java)
            intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentToMain)
            finish() }

        userCompleteInfo()

        edit_calendar.setOnClickListener {
            showDatePickerDialog()
        }

    }

    private fun showDatePickerDialog(){
        val newFragment = TimePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->

            val seletedDate = day.toString() + "/" + (month+1) + "/" + year
            date_pick.setText(seletedDate)

        })
        newFragment.show(supportFragmentManager, "datePicker")
    }


    private fun uploadImage(){

        when{
            imageUri == null -> Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(edit_description_post.text.toString()) -> Toast.makeText(this, "write a description", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(edit_post.text.toString()) -> Toast.makeText(this, "write a Title", Toast.LENGTH_SHORT).show()

            else -> {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Add new Post")
                progressDialog.setMessage("Please wait")
                progressDialog.show()

                val fileRef = storageProfileRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){

                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> {task ->

                    if (task.isSuccessful){

                        val downloadUrl = task.result
                        URL = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Post")
                        val postId = ref.push().key
                        val postAux = HashMap<String, Any>()
                        postAux["pid"] = postId!!
                        postAux["title"] = edit_post.text.toString()
                        postAux["description"] = edit_description_post.text.toString()
                        postAux["username"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postAux["date"] = date_pick.text.toString()
                        postAux["image"] = URL

                        ref.child(postId).updateChildren(postAux)
                        val intentToMain = Intent(this, MainActivity::class.java)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
                        startActivity(intentToMain)
                        finish()
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        }

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
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale).into(show_image_tweeting)
                }
            }
        })
    }

    private fun requestPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), READ_STORAGE_AGREE);
            /* if (ActivityCompat.shouldShowRequestPermissionRationale(PostActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
               /* val snackbar : Snackbar = make(edit_post, "You should accept the permissions to upload photos to your feed", Snackbar.LENGTH_SHORT)
                 snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                 snackbar.show()*/

             }else{*/
        }else {
            CropImage.activity().setAspectRatio(1,1).start(this)
        }

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_STORAGE_AGREE);
        }else {
            //CropImage.activity().setAspectRatio(1,1).start(this)
        }*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            READ_STORAGE_AGREE -> {
                if ((grantResults.isNotEmpty()) && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        CropImage.activity().setAspectRatio(1, 1).start(this)
                    } else {
                        Toast.makeText(applicationContext, "No can put images", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            WRITE_STORAGE_AGREE -> {
                if ((grantResults.isNotEmpty()) && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            applicationContext,
                            "Permissions to WRITE OK",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Permissions to WRITE NOT OK",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            // super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}