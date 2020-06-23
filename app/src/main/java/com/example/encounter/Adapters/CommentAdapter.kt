package com.example.encounter.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Model.Comments
import com.example.encounter.Model.Users
import com.example.encounter.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CommentAdapter (private var mContext: Context,
                      private var aComment : MutableList<Comments>?) : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return CommentAdapter.ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return aComment!!.size
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val commentario = aComment!![position]
        holder.comment.text = commentario.getComment()

        getUserInfo(holder.imageProfile, holder.username, commentario.getCommenter())

    }

    class ViewHolder (@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){

        var imageProfile : ImageView = itemView.findViewById(R.id.imageprofile_item_comment)
        var username :TextView = itemView.findViewById(R.id.username_item_comment)
        var comment : TextView = itemView.findViewById(R.id.description_item_comment)

    }

    private fun getUserInfo(imageProfile: ImageView, username: TextView, commenter: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(commenter)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue(Users::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale).into(imageProfile)
                    username.text = user.getUsername()
                }
            }
        })
    }

}