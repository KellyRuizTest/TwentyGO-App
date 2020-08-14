package com.encounter.twenty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.encounter.twenty.Adapters.UserAdapter
import com.encounter.twenty.Model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_followings.*

class FollowingsActivity : AppCompatActivity() {

    private var recyclerView : RecyclerView? = null
    private var userAdapter : UserAdapter? = null
    private var listUser : List<Users>? = null
    private var idList: List<String>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var typefrom : String? = null
    private var idPost_idUser : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followings)

        typefrom = intent.getStringExtra("type")
        idPost_idUser = intent.getStringExtra("pid")

        recyclerView = findViewById(R.id.recycler_followers)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        listUser = ArrayList()
        userAdapter = UserAdapter(this, listUser as ArrayList<Users>, false)
        recyclerView?.adapter = userAdapter


        idList = ArrayList()

        if (typefrom != null){

            if (typefrom.equals("following")){
                retrieveUserFollowings(idPost_idUser!!)
                id_profile_follower!!.text = "Followings" }

            if(typefrom.equals("follower")){
                retrieveUserFollewers(idPost_idUser!!)
                id_profile_follower!!.text = "Followers" }

            if (typefrom.equals("likes")){
                retrieveLikes(idPost_idUser!!)
                id_profile_follower!!.text = "Likes"
            }
        }
    }

   /* private val onItemClickListener= object : UserAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int, idUser: Users) {
            Toast.makeText(applicationContext, "Clicked " + position + idUser.getPid()+ "", Toast.LENGTH_SHORT).show()
            //(contexto as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.content_fragment, PerfilFragment()).commit()
/*
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.content_fragment, fragment).commit()
            val intent = Intent(context, DetailActivity::class.java)
            val placeImage = view.findViewById<ImageView>(R.id.image_post_from)
            val placeNameHolder = view.findViewById<LinearLayout>(R.id.placeHolder)

            //val imageViewPair = Pair.create<placeImage as View, "tImage")

            val options =
                activity?.let { ActivityOptionsCompat.makeSceneTransitionAnimation(it, Pair.create(image_post_from, "tbImage")) }
            intent.putExtra("pid", idPost.getPid())

            context?.startActivity(intent, options?.toBundle())*/
        }
    }*/

    private fun retrieveLikes(id: String) {

        val likeRef = FirebaseDatabase.getInstance().reference.child("Likes").child(id)

        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){
                    (idList as ArrayList<String>).clear()

                    for (snapshot in p0.children){
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }

                    showUser()
                }
            }
        })
    }

    private fun showUser() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (listUser as ArrayList<Users>).clear()

                for (snapshoy in dataSnapshot.children) {
                    val user = snapshoy.getValue(Users::class.java)
                    for (id in idList!!) {

                        if (user?.getPid() == id) {
                            (listUser as ArrayList<Users>).add(user)
                        }
                    }
                }
                userAdapter?.notifyDataSetChanged()

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun retrieveUserFollewers(idUser : String){

        val userRef = FirebaseDatabase.getInstance().reference.child("Follow").child(idUser).child("followers")
        userRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (listUser as ArrayList<Users>).clear()
                for(snapshot in dataSnapshot.children){
                    val userSnapshot = snapshot.key
                    val userFollower = FirebaseDatabase.getInstance().reference.child("Users").child(userSnapshot!!)

                    userFollower.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (p0.exists()){
                                // listUser?.clear()
                                val eachFollowers = p0.getValue(Users::class.java)
                                (listUser as ArrayList<Users>).add(eachFollowers!!)
                            }
                            // Collections.reverse(listUser)
                            userAdapter!!.notifyDataSetChanged()
                        }
                    })
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun retrieveUserFollowings(idUser : String){

        val userRef = FirebaseDatabase.getInstance().reference.child("Follow").child(idUser).child("following")
        userRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (listUser as ArrayList<Users>).clear()
                for(snapshot in dataSnapshot.children){
                    val userSnapshot = snapshot.key
                    val userFollower = FirebaseDatabase.getInstance().reference.child("Users").child(userSnapshot!!)

                    userFollower.addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (p0.exists()){
                                // listUser?.clear()
                                val eachFollowers = p0.getValue(Users::class.java)
                                (listUser as ArrayList<Users>).add(eachFollowers!!)
                            }
                            // Collections.reverse(listUser)
                            userAdapter!!.notifyDataSetChanged()
                        }
                    })
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}