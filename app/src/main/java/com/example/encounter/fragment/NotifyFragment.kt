package com.example.encounter.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Adapters.JoinAdapter
import com.example.encounter.Adapters.MyPostAdapter
import com.example.encounter.Model.Post
import com.example.encounter.Model.Users
import com.example.encounter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.show_joins.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotifyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotifyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var postsList : List<Post>? = null
    var mypostJoinAdater : JoinAdapter? = null
    private var idList: List<String>? = null

    private var firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

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
        val view = inflater.inflate(R.layout.fragment_notify, container, false)


        var recyclerViewPostimages : RecyclerView = view.findViewById(R.id.recycler_view_joins)
        recyclerViewPostimages.setHasFixedSize(true)
        recyclerViewPostimages.layoutManager =LinearLayoutManager(context)

        postsList = ArrayList()
        idList = ArrayList()
        mypostJoinAdater = JoinAdapter(view.context, postsList as ArrayList)
        recyclerViewPostimages.adapter = mypostJoinAdater

        retrievePost()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotifyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotifyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun retrievePost(){

        val likeRef = FirebaseDatabase.getInstance().reference.child("Join")

        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    (idList as ArrayList<String>).clear()
                    for (snapshot in p0.children) {
                        if (snapshot.child(firebaseUser!!.uid).exists()) {
                            (idList as ArrayList<String>).add(snapshot.key!!)
                        }
                    }

                    showPost()
                }
            }
        })
    }

    private fun showPost() {
       val posRef = FirebaseDatabase.getInstance().reference.child("Post")
        posRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                (postsList as ArrayList<Post>).clear()

                for (snapship in p0.children){
                    val post = snapship.getValue(Post::class.java)
                    for (id in idList!!){
                        if (post?.getPid() == id){
                            (postsList as ArrayList<Post>).add(post)
                        }
                    }
                }
                mypostJoinAdater?.notifyDataSetChanged()
            }

        })
    }



}