package com.encounter.twenty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.encounter.twenty.Adapters.CommentAdapter
import com.encounter.twenty.Model.Comments
import com.encounter.twenty.Model.Post
import com.encounter.twenty.Model.Users
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {

    private var idpost = ""
    private var commenter = ""
    private var commentAdapter : CommentAdapter? = null
    private var commentList: MutableList<Comments>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val bundle = intent.extras
        idpost = intent.getStringExtra("pid")
        commenter = intent.getStringExtra("user")

        println(idpost)
        println(commenter)

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

                val snackbar: Snackbar = make(comment_button, "Text is empty", Snackbar.LENGTH_SHORT)
                snackbar.setAnchorView(bootom_appbar)
                snackbar.show()
             //   Toast.makeText(this@CommentActivity, "Text is empty.", Toast.LENGTH_SHORT).show()
            }else{
                addComment()
            }
        })
    }

    private fun addComment() {

        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(idpost)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment.text.toString()
        commentsMap["commenter"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)
        add_comment!!.text?.clear()

    }

    // This is ok method
    private fun userImageInfo() {

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser!!.uid)

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

    // This is ok method
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
            override fun onCancelled(p0: DatabaseError) {}

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