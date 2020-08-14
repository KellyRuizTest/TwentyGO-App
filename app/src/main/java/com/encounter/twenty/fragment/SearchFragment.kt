package com.encounter.twenty.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.encounter.twenty.Adapters.UserAdapter
import com.encounter.twenty.Model.Users
import com.encounter.twenty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //

    private var recyclerView : RecyclerView? = null
    private var userAdapter : UserAdapter? = null
    private var listUser : MutableList<Users>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(  inflater: LayoutInflater,
                                container: ViewGroup?,
                                savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // add view: View
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        val activity = getActivity() as Context
        recyclerView = view.findViewById(R.id.recycler_search_users)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        listUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, listUser as ArrayList<Users>, true) }
        recyclerView?.adapter = userAdapter

        view.search_user_editext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.search_user_editext.toString() == ""){

                }else
                {
                    recyclerView?.visibility = View.VISIBLE
                    retrieveUser()
                    searchUser(p0.toString())
                }
            }
        })
        return view
    }

    private fun retrieveUser(){

        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (view?.search_user_editext.toString() == ""){
                    listUser?.clear()

                    for(snapshot in dataSnapshot.children){
                        val userSnapshot = snapshot.getValue(Users::class.java)
                        if (userSnapshot != null){
                            if (userSnapshot.getPid() == firebaseUser!!.uid){
                                println("Im the same user, so I dont find it")
                            }else{
                                listUser?.add(userSnapshot)
                            }

                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

   private fun searchUser(input : String){
       val queryToSearch = FirebaseDatabase.getInstance().reference.child("Users")
           .orderByChild("username").startAt(input).endAt(input + "\uf8ff")

       queryToSearch.addValueEventListener(object : ValueEventListener {
           override fun onDataChange(dataSnapshot: DataSnapshot) {
               listUser?.clear()
               for (snapshot in dataSnapshot.children){
                   val userSnap = snapshot.getValue(Users::class.java)
                   if (userSnap != null){
                       listUser?.add(userSnap)
                   }
               }
           }

           override fun onCancelled(p0: DatabaseError) {
           }
       })
   }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

