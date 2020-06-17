package com.example.encounter.Adapters

import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Model.Post
import com.example.encounter.Model.Users
import com.example.encounter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class PostAdapter(
                    private val mContext: Context,
                    private val listPost: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>()

{

    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.PostViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.activity_encounter, parent, false)
        return PostAdapter.PostViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listPost.size
    }

    class PostViewHolder (@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){

        var profileImage : ImageView = itemView.findViewById(R.id.avatar_image)
        var image : ImageView = itemView.findViewById(R.id.image_post_from)
        var likeButton : ImageView = itemView.findViewById(R.id.like_post)
        var joinButton : ImageView = itemView.findViewById(R.id.join_post)
        var commetButton : ImageView = itemView.findViewById(R.id.comment_post)

        var idUser : TextView = itemView.findViewById(R.id.name_user)
        var title : TextView = itemView.findViewById(R.id.post_title)
        var descriptionPost : TextView = itemView.findViewById(R.id.post_description)

        var countComen : TextView = itemView.findViewById(R.id.count_comment)
        var countLike : TextView = itemView.findViewById(R.id.count_like)
        var countJoin : TextView = itemView.findViewById(R.id.join_like)

    }

    override fun onBindViewHolder(holder: PostAdapter.PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = listPost[position]
        Picasso.get().load(post.getImage()).into(holder.image)
        holder.title.text = post.getTitle()
        holder.descriptionPost.text = post.getDescription()

        userInfo(holder.profileImage, holder.idUser, post.getUsername())

    }

    private fun userInfo(profileImage: ImageView, idUser: TextView, pid: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(pid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    val user = dataSnapshot.getValue<Users>(Users::class.java)

                    println("=========================================================================")
                    println("=========================================================================")
                    println(user!!.getName() + "/" + user!!.getUsername() + "/" + user!!.getEmail())
                    println("=========================================================================")
                    println("=========================================================================")
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale)
                        .into(profileImage)
                    idUser.text = user.getUsername()

                }
            }

        })
    }
}
