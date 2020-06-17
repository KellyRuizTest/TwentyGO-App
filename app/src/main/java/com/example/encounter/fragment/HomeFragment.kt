package com.example.encounter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Adapters.PostAdapter
import com.example.encounter.Model.Post
import com.example.encounter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null


    private var postAdapter : PostAdapter? = null
    private var postList : MutableList<Post>? = null
    private var followingList : MutableList<Post>? = null
    private var recyclerView : RecyclerView? = null

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_home)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView?.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView?.adapter = postAdapter

       checkFollowing()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun checkFollowing(){

        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference.child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (followingList as ArrayList<String>).clear()
                    for (datasnaphot in p0.children){

                        println("------------------------------------------------------------------------------------------>")
                        println(datasnaphot.key)
                        println("------------------------------------------------------------------------------------------>")
                        datasnaphot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    retrivePosts()
                }
            }
        })
    }

    private fun retrivePosts(){
        val postRef = FirebaseDatabase.getInstance().reference.child("Post")

        postRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
               postList?.clear()

                for (datasnaphot in p0.children){
                    val post = datasnaphot.getValue(Post::class.java)
                    for (userId in (followingList as ArrayList<String>)){
                        if (post!!.getUsername().equals(userId)){
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}