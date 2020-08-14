package com.encounter.twenty.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.encounter.twenty.Model.Post
import com.encounter.twenty.R
import com.encounter.twenty.fragment.PostDetailFragment
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