package com.example.encounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.encounter.Model.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_setting.*

class CommentActivity : AppCompatActivity() {


    private var idpost = ""
    private var commenter = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intentget = intent
        idpost = intent.getStringExtra("pid")
        commenter = intent.getStringExtra("user")

        userImageInfo()

        comment_button.setOnClickListener( View.OnClickListener {
            if (add_comment!!.text.toString() == ""){
                Toast.makeText(this@CommentActivity, "Text is empty.", Toast.LENGTH_SHORT).show()
            }else{
                addComment()
            }
        })
    }

    private fun addComment() {

        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(idpost)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment.text.toString()
        commentsMap["commenter"] = commenter

        commentsRef.push().setValue(commentsMap)

        add_comment!!.text.clear()


    }

    private fun userImageInfo() {

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users")
            .child(commenter.toString())
        userInfo.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    val user = p0.getValue<Users>(Users::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale)
                        .into(imageprofile_comment)

                }
            }
        })
    }


}