package com.example.encounter.Adapters

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.encounter.CommentActivity
import com.example.encounter.FollowingsActivity
import com.example.encounter.MainActivity
import com.example.encounter.Model.Post
import com.example.encounter.Model.Users
import com.example.encounter.R
import com.example.encounter.fragment.PerfilFragment
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
        var commentButton : ImageView = itemView.findViewById(R.id.comment_post)

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
        isLike(post.getPid(), holder.likeButton)
        numberOfLikes(holder.countLike, post.getPid())
        numberOfComments(holder.countComen, post.getPid())

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Likes"){
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPid()).child(firebaseUser!!.uid).setValue(true)
            }else{
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPid()).child(firebaseUser!!.uid).removeValue()
                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }

        holder.commentButton.setOnClickListener {
            val intentToComment = Intent(mContext, CommentActivity::class.java)
            intentToComment.putExtra("pid", post.getPid())
            intentToComment.putExtra("user", post.getUsername())
            mContext.startActivity(intentToComment)
        }

        holder.countLike.setOnClickListener {

            val intentToShowuser = Intent(mContext, FollowingsActivity::class.java)
            intentToShowuser.putExtra("type", "likes")
            intentToShowuser.putExtra("pid", post.getPid())
            mContext.startActivity(intentToShowuser)
        }

        holder.countComen.setOnClickListener {
            val intentToComment = Intent(mContext, CommentActivity::class.java)
            intentToComment.putExtra("pid", post.getPid())
            intentToComment.putExtra("user", post.getUsername())
            mContext.startActivity(intentToComment)
        }

        holder.idUser.setOnClickListener {
            val send_id = mContext.getSharedPreferences("ID", Context.MODE_PRIVATE).edit()
            send_id.putString("userID", post.getUsername())
            send_id.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.content_fragment, PerfilFragment()).commit()
        }
    }

    private fun numberOfLikes(countLike: TextView, pid: String) {

        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(pid)
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    countLike.text = p0.childrenCount.toString() + " Likes"
                }
            }
        })
    }

    private fun numberOfComments(countComment: TextView, pid: String) {

        val commentRef = FirebaseDatabase.getInstance().reference.child("Comments").child(pid)
        commentRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    countComment.text = "view all " + p0.childrenCount.toString() + " comments"
                }
            }
        })
    }

    private fun isLike(pid: String, likeButton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(pid)
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){

                    likeButton.setImageResource(R.drawable.ic_like_liked)
                    likeButton.tag = "Liked"

                }else{

                    likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    likeButton.tag = "Likes"

                }
            }

        })
    }

    private fun userInfo(profileImage: ImageView, idUser: TextView, pid: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(pid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    val user = dataSnapshot.getValue<Users>(Users::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.usermale)
                        .into(profileImage)
                    idUser.text = user.getUsername()

                }
            }

        })
    }
}
