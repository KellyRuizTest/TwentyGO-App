package com.example.encounter.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.FolloworsActivity
import com.example.encounter.Model.Users
import com.example.encounter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserAdapter(private var contexto : Context,
                  private var listUsers : List<Users>,
                  private var isFragment : Boolean = false
                  ) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(contexto).inflate(R.layout.show_users, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {

        val user = listUsers[position]
        holder.Name.text = user.getName()
        holder.userName.text = user.getUsername()
        holder.bio.text = user.getBio()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.usermale).into(holder.image)

        isFollowing(holder.followButton, user.getPid())

        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString() == "Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                        .child("following").child(user.getPid()).setValue(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(user.getPid())
                                        .child("followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                            }
                                        }
                                }
                            }
                        }

                }
            } else {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                        .child("following")
                        .child(user.getPid()).removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(user.getPid())
                                        .child("followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }

        holder.cardView.setOnClickListener {

            val intent_touser = Intent(contexto, FolloworsActivity::class.java).apply {
                putExtra("UserID", user.getPid())
            }
            contexto.startActivity(intent_touser)
        }

    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userName: TextView = itemView.findViewById(R.id.show_contact_id)
        var Name: TextView = itemView.findViewById(R.id.show_contact_name)
        var bio: TextView = itemView.findViewById(R.id.show_bio)
        var image: ImageView = itemView.findViewById(R.id.show_contact_image)
        var followButton: Button = itemView.findViewById(R.id.button_follow)

        var cardView : CardView = itemView.findViewById(R.id.cardview_show_contact)

    }

    private fun isFollowing(followButton : Button, auxUser : String) {

        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString()).child("following")
        }

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(auxUser).exists()) {

                    Log.i("What", "<------What is "+ dataSnapshot.child(auxUser)+"--------------->")
                    followButton.text = "Following"
                } else {
                    followButton.text = "Follow"
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }
}