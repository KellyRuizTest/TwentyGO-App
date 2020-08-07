package com.example.encounter.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Model.Post
import com.example.encounter.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class JoinAdapter(
                    private val mContext: Context,
                    private val listPost: List<Post> ): RecyclerView.Adapter<JoinAdapter.JoinViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.show_joins, parent, false)
        return JoinViewHolder(view)
    }

    override fun getItemCount(): Int {
       return listPost.size
    }

    override fun onBindViewHolder(holder: JoinViewHolder, position: Int) {
        val join = listPost[position]

        Picasso.get().load(join.getImage()).into(holder.imagePost)
        holder.titlePost.text = join.getTitle()
        holder.descriptionPost.text = join.getDescription()
        if (join.getDate() != null){ holder.datePost.text = join.getDate()}

        getCountJoined(join.getPid(), holder.count)

    }

    private fun getCountJoined(pid: String, count: TextView) {

        val joinRef = FirebaseDatabase.getInstance().reference.child("Join").child(pid)
        joinRef.addValueEventListener( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){ count.text = ""+p0.childrenCount }
            }
        })

    }


    inner class JoinViewHolder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView) {

        var imagePost : ImageView = itemView.findViewById(R.id.image_post_from)
        var titlePost : TextView = itemView.findViewById(R.id.title_join)
        var descriptionPost : TextView = itemView.findViewById(R.id.description_joins)
        var username : TextView = itemView.findViewById(R.id.user_join)
        var count : TextView = itemView.findViewById(R.id.count_joins)
        var datePost : TextView = itemView.findViewById(R.id.date)
    }

}