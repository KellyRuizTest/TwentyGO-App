package com.example.encounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Adapters.CommentAdapter
import com.example.encounter.Model.Comments
import com.example.encounter.Model.Post
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
    private var commentAdapter : CommentAdapter? = null
    private var commentList: MutableList<Comments>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intentget = intent
        idpost = intent.getStringExtra("pid")
        commenter = intent.getStringExtra("user")

        val recyclerView : RecyclerView = findViewById(R.id.recycler_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter

        userImageInfo()
        retrieveCommens()
        postImageInfo()

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

    private fun postImageInfo() {

        val postInfo = FirebaseDatabase.getInstance().reference.child("Post")
            .child(idpost)

        postInfo.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    val post = p0.getValue<Post>(Post::class.java)

                    Picasso.get().load(post!!.getImage()).placeholder(R.drawable.usermale)
                        .into(post_image_comment)
                }
            }
        })
    }

    private fun retrieveCommens(){
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(idpost)

        commentsRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists()){

                    commentList!!.clear()
                    for (snapshot in p0.children){
                        val commentEach = snapshot.getValue(Comments::class.java)
                        commentList!!.add(commentEach!!)
                    }

                   commentAdapter!!.notifyDataSetChanged()
               }
            }


        })
    }


}