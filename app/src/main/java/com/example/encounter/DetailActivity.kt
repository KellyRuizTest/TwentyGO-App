package com.example.encounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_perfil.view.*

class DetailActivity : AppCompatActivity() {

    private var idPost : String? = null
    private var commenter = ""
    private var commentAdapter : CommentAdapter? = null
    private var commentList: MutableList<Comments>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        idPost = intent.getStringExtra("pid")
        retrivePostInfo()

        val recyclerView : RecyclerView = findViewById(R.id.recycler_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter

       // userImageInfo()
       retrieveCommens()

    }

    private fun retrivePostInfo(){
        val postRef = FirebaseDatabase.getInstance().reference.child("Post").child(idPost!!)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val post = p0.getValue<Post>(Post::class.java)
                    Picasso.get().load(post!!.getImage()).placeholder(R.drawable.usermale).into(image_post_detail)
                    post_title.text = post.getTitle()
                    post_description.text = post.getDescription()
                    retrieveUserInfo(post.getUsername())
                }
            }
        })
    }

    private fun retrieveUserInfo(id : String){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(id)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<Users>(Users::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale).into(avatar_image)
                    name_user.text = user.getUsername()
                }
            }
        })
    }

    private fun retrieveCommens(){
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(idPost!!)

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