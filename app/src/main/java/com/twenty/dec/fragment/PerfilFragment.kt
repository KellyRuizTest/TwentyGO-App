package com.twenty.dec.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twenty.dec.Adapters.MyPostAdapter
import com.twenty.dec.FollowingsActivity
import com.twenty.dec.LoginActivity
import com.twenty.dec.Model.Post
import com.twenty.dec.Model.Users
import com.twenty.dec.R
import com.twenty.dec.SettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.view.*
import kotlinx.android.synthetic.main.fragment_perfil.view.posts
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var firebaseUser: FirebaseUser

    var postsList: List<Post>? = null
    var mypostAdater: MyPostAdapter? = null
    lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("ID", Context.MODE_PRIVATE)
        if (pref != null) {
            userID = pref.getString("userID", "none").toString()

            if (userID.equals("none")) {
                userID = firebaseUser.uid.toString()
            }
        }

        if (userID != firebaseUser.uid) {
            view.edit_profile.visibility = View.GONE
            view.logout_btn.visibility = View.INVISIBLE
            checkFollowingStatusButton()

        } else {
            view.following_button.visibility = View.GONE
            view.mensaje.visibility = View.GONE
        }

        view.edit_profile.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    SettingActivity::class.java
                )
            )
        }

        view.following_button.setOnClickListener {

            if (view?.following_button?.text.toString() == "Follow") {
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following").child(userID).setValue(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseDatabase.getInstance().reference.child("Follow")
                                .child(userID)
                                .child("followers").child(firebaseUser.uid)
                                .setValue(true)
                        }
                    }
                view?.following_button?.text = "Following"
            } else {

                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following").child(userID).removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseDatabase.getInstance().reference.child("Follow")
                                .child(userID)
                                .child("followers").child(firebaseUser.uid)
                                .removeValue()

                        }
                    }
                view?.following_button?.text = "Follow"
            }

        }

        view.logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intentToFinish = Intent(context, LoginActivity::class.java)
            intentToFinish.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentToFinish)
        }

        var recyclerViewPostimages: RecyclerView
        recyclerViewPostimages = view.findViewById(R.id.recycler_view_own_post)
        recyclerViewPostimages.setHasFixedSize(true)

        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewPostimages.layoutManager = linearLayoutManager

        postsList = ArrayList()
        mypostAdater = context?.let { MyPostAdapter(it, postsList as ArrayList<Post>) }
        recyclerViewPostimages.adapter = mypostAdater

        allPosts()
        getFollowersCount()
        getFollowingsCount()
        userCompleteInfo()
        getPostsCount()

        view.follower_space.setOnClickListener {
            val intentToShowuser = Intent(context, FollowingsActivity::class.java)
            intentToShowuser.putExtra("type", "follower")
            intentToShowuser.putExtra("pid", userID)
            context?.startActivity(intentToShowuser)
        }

        view.following_space.setOnClickListener {
            val intentToShowuser = Intent(context, FollowingsActivity::class.java)
            intentToShowuser.putExtra("type", "following")
            intentToShowuser.putExtra("pid", userID)
            context?.startActivity(intentToShowuser)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getFollowersCount() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(userID!!).child("followers")


        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view?.followers?.text = p0.childrenCount.toString()
                }
            }
        })
    }

    private fun checkFollowingStatusButton(){
        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(firebaseUser.uid).child("following")

        followingRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(userID).exists()) {
                    view?.following_button?.text = "Following"
                    view?.following_button?.setBackgroundColor(Color.WHITE)
                    view?.following_button?.setBackgroundResource(R.drawable.borde_redondo_tweet)
                } else {
                    view?.following_button?.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun getFollowingsCount(){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(userID!!).child("following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    view?.following?.text = p0.childrenCount.toString()
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

                        if (postone.getUsername().equals(userID)){
                            (postsList as ArrayList<Post>).add(postone)
                        }
                        Collections.reverse(postsList)
                        mypostAdater!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }


    private fun userCompleteInfo(){

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users").child(userID)
        userInfo.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<Users>(Users::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale).into(view!!.profile_image)
                    view?.name_profile?.text = user.getName()
                    view?.description_profile?.text = user.getBio()
                    view?.id_profile_frag?.text = user.getUsername()
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
                        if (postInfo!!.getUsername().equals(userID)){
                            count++
                        }
                    }
                    if (count > 0){
                        view?.posts?.text = ""+count+"" } else {
                        view?.posts?.text = ""+0+""
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("ID", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userID", firebaseUser.uid)
        pref?.apply()
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("ID", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userID", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("ID", Context.MODE_PRIVATE)?.edit()
        pref?.putString("userID", firebaseUser.uid)
        pref?.apply()
    }
}