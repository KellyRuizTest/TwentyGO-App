package com.example.encounter.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Adapters.MyPostAdapter
import com.example.encounter.LoginActivity
import com.example.encounter.Model.Post
import com.example.encounter.Model.Users
import com.example.encounter.R
import com.example.encounter.SettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.*
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
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    var postsList : List<Post>? = null
    var mypostAdater : MyPostAdapter? = null

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
        val view =  inflater.inflate(R.layout.fragment_perfil, container, false)
        view.edit_profile.setOnClickListener { startActivity(Intent(context, SettingActivity::class.java)) }

        getFollowers()
        getFollowing()
        userCompleteInfo()
        getPostsCount()

        var recyclerViewPostimages : RecyclerView
        recyclerViewPostimages = view.findViewById(R.id.recycler_view_own_post)
        recyclerViewPostimages.setHasFixedSize(true)

        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewPostimages.layoutManager = linearLayoutManager

        postsList = ArrayList()
        mypostAdater = context?.let { MyPostAdapter(it, postsList as ArrayList<Post>) }
        recyclerViewPostimages.adapter = mypostAdater

        allPosts()

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

    private fun getFollowers(){
        val followersRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString()).child("followers")
        }

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    view?.followers?.text = p0.childrenCount.toString()
                }
            }
        })
    }

    private fun getFollowing(){
        val followersRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString()).child("following")
        }

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

                        println("<=========================================================================>")
                        println("firebaseUser: "+firebaseUser!!.uid)
                        println("postone: "+postone.getUsername())
                        println("<=========================================================================>")

                        if (postone.getUsername().equals(firebaseUser!!.uid)){
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

        val userInfo = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser?.uid.toString())
        userInfo.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    val user = p0.getValue<Users>(Users::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale).into(view?.profile_image)
                    view?.name_profile?.text = user!!.getName()
                    view?.description_profile?.text = user!!.getBio()
                    view?.id_profile_frag?.text = user!!.getUsername()
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
                        if (postInfo!!.getUsername().equals(firebaseUser!!.uid.toString())){
                            count++
                        }
                    }
                    posts.text = ""+count+""
                }
            }


        })
    }
}