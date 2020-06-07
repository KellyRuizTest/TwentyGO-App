package com.example.encounter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_post.*


class PostActivity : AppCompatActivity() {

    private var URL = ""
    private var checkif = false
    private var imageUri : Uri? = null
    private var storageProfileRef : StorageReference? = null
    private lateinit var  firebaseUser : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfileRef = FirebaseStorage.getInstance().reference.child("Post Pictures")

        button_postingclick.setOnClickListener { uploadImage() }

        CropImage.activity().setAspectRatio(1,1).start(this)

        back_intent.setOnClickListener { val intentToMain = Intent(this, MainActivity::class.java)
            intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentToMain)
            finish() }

    }


    private fun uploadImage(){

        when{
            imageUri == null -> Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(editPost.text.toString()) -> Toast.makeText(this, "write a description", Toast.LENGTH_SHORT).show()

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
                        postAux["description"] = editPost.text.toString()
                        postAux["username"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postAux["image"] = URL

                        ref.child(postId).updateChildren(postAux)
                        val intentToMain = Intent(this, MainActivity::class.java)
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

}