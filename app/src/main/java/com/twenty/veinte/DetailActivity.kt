package com.twenty.veinte

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twenty.veinte.Adapters.CommentAdapter
import com.twenty.veinte.Model.Comments
import com.twenty.veinte.Model.Post
import com.twenty.veinte.Model.Users
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private var idPost : String? = null
    private var commenter = ""
    private var commentAdapter : CommentAdapter? = null
    private var commentList: MutableList<Comments>? = null
    private var firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

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

        val istheSame : Boolean

        areTheSameShowremoveIcon()
        retrieveCommens()

        remove_post.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("Post").child(idPost!!).removeValue()
            val snackbar: Snackbar = Snackbar.make(name_user, "deleted post" + "", Snackbar.LENGTH_SHORT
            )
            snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            snackbar.setDuration(1000)
            // snackbar.anchorView = holder.descriptionPost
            snackbar.show()

            val intent = Intent(applicationContext, MainActivity::class.java)
            //Thread.sleep(2000)

            startActivity(intent)
            overridePendingTransition(R.anim.fade_in_more, R.anim.fade_out_more)
            finish()
        }


    }

    private fun removePost() {



    }

    private fun areTheSameShowremoveIcon() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Post").child(idPost!!)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val post = p0.getValue<Post>(Post::class.java)

                    if (post?.getUsername() == firebaseUser.uid.toString()){
                        println("<========================================================>")
                        println(""+ post?.getUsername()+"***")
                        println("***"+firebaseUser.uid.toString())
                        remove_post.visibility = View.VISIBLE
                    }
                }
            }
        })


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