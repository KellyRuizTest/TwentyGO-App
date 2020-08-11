package com.example.encounter.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.Model.Post
import com.example.encounter.R
import com.example.encounter.R.layout.fragment_home
import com.example.encounter.R.layout.fragment_perfil
import com.example.encounter.fragment.PerfilFragment
import com.example.encounter.fragment.PostDetailFragment
import com.squareup.picasso.Picasso

class MyPostAdapter(private val Contexto: Context, private val listPost : List<Post>) : RecyclerView.Adapter<MyPostAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostAdapter.ViewHolder {
        val view = LayoutInflater.from(Contexto).inflate(R.layout.show_post_item, parent, false)
        return MyPostAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listPost.size
    }


    override fun onBindViewHolder(holder: MyPostAdapter.ViewHolder, position: Int) {

        val post = listPost[position]
        Picasso.get().load(post.getImage()).into(holder.post_image)

        holder.post_image.setOnClickListener {
            val send_id = Contexto.getSharedPreferences("ID", Context.MODE_PRIVATE).edit()
            send_id.putString("postId", post.getPid())
            send_id.putString("userID", post.getUsername())
            send_id.apply()

            /*val manager = (Contexto as FragmentActivity).supportFragmentManager
            manager.findFragmentById(fragment_perfil)*/

            (Contexto as FragmentActivity).supportFragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.content_fragment, PostDetailFragment())
                .commit()
        }
    }

    class ViewHolder(@NonNull itemView :  View) : RecyclerView.ViewHolder(itemView){
        var post_image : ImageView =  itemView.findViewById(R.id.post_item)
    }

}