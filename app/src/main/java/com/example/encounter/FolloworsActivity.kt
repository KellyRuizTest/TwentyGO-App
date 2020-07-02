package com.example.encounter

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Adapters.MyPostAdapter
import com.example.encounter.Model.Post
import com.example.encounter.Model.Users
import com.example.encounter.R.drawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class FolloworsActivity : AppCompatActivity() {

    private var Userid : String? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    var postsList : List<Post>? = null
    var mypostAdater : MyPostAdapter? = null

    var checkedUsername : TextView? = null
    var checkedName : TextView? = null
    var checkedescription : TextView? = null
    var checkedImage : ImageView? = null
    var checkepostcount : TextView? = null
    var checkedfollowers : TextView? = null
    var checkedfollowings : TextView? = null

    var checkedfollowed : Button? = null

    var isFollowed : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followors)

        Userid = intent.extras?.getString("UserID")

        if (Userid != null){

            var iduser : TextView = findViewById(R.id.id_follower_frag)
            iduser.text = Userid

            checkedUsername = findViewById(R.id.id_follower_frag)
            checkedName = findViewById(R.id.name_follower)
            checkedescription = findViewById(R.id.description_follower)
            checkedImage = findViewById(R.id.profile_follower)
            checkepostcount = findViewById(R.id.posts_followers)
            checkedfollowers = findViewById(R.id.followers_followers)
            checkedfollowings = findViewById(R.id.following_followers)
            checkedfollowed = findViewById(R.id.following_button)


            var recyclerViewPostimages : RecyclerView = findViewById(R.id.recycler_view_follower_post)
            recyclerViewPostimages.setHasFixedSize(true)

            val linearLayoutManager : LinearLayoutManager = GridLayoutManager(applicationContext,3)
            recyclerViewPostimages.layoutManager = linearLayoutManager
            postsList = ArrayList()
            mypostAdater = applicationContext?.let { MyPostAdapter(it, postsList as ArrayList<Post>) }
            recyclerViewPostimages.adapter = mypostAdater

            allPosts()
            userCompleteInfo()
            getPostsCount()
            getFollowers()
            getFollowing()

        }

    }

    private fun userCompleteInfo(){

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users").child(Userid!!)
        userInfo.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<Users>(Users::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(drawable.usermale).into(checkedImage)
                    checkedName?.text = user!!.getName()
                    checkedescription?.text = user!!.getBio()
                    checkedUsername?.text = user!!.getUsername()
                }
            }
        })
    }

    private fun allPosts(){

        val postRef = FirebaseDatabase.getInstance().reference.child("Post")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    (postsList as ArrayList<Post>).clear()

                    for (eachone in p0.children){
                        val postone = eachone.getValue(Post::class.java)!!

                        println("<=========================================================================>")
                        println("firebaseUser: "+firebaseUser!!.uid)
                        println("postone: "+postone.getUsername())
                        println("<=========================================================================>")

                        if (postone.getUsername().equals(Userid)){
                            (postsList as ArrayList<Post>).add(postone)
                        }
                        Collections.reverse(postsList)
                        mypostAdater!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun getPostsCount(){
        val postcountRef = FirebaseDatabase.getInstance().reference.child("Post")

        postcountRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    var count = 0
                    for (snap in p0.children){
                        val postInfo = snap.getValue(Post::class.java)
                        if (postInfo!!.getUsername().equals(Userid)){
                            count++
                        }
                    }
                    checkepostcount?.text = "" + count + ""


                }
            }

        })
    }

    private fun getFollowers(){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(Userid!!).child("followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    checkedfollowers?.text = p0.childrenCount.toString()

                    for (datasnapshot in p0.children){
                        val userforcheck = datasnapshot.key

                        if (userforcheck!!.equals(firebaseUser?.uid)){
                            println("<==============================================================>")
                            println("Just I want to print something")
                            println("<==============================================================>")
                            checkedfollowed!!.text = ""+ "Following"
                            checkedfollowed!!.setBackgroundColor(Color.WHITE)
                            checkedfollowed!!.setBackgroundResource(R.drawable.borde_round_follow)
                            isFollowed = true
                        }
                    }
                }
            }
        })
    }

    private fun getFollowing(){
        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(Userid!!).child("following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    checkedfollowings?.text = p0.childrenCount.toString()
                }
            }
        })
    }



}