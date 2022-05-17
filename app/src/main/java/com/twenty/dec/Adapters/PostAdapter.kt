package com.twenty.dec.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.twenty.dec.CommentActivity
import com.twenty.dec.FollowingsActivity
import com.twenty.dec.Model.Post
import com.twenty.dec.Model.Users
import com.twenty.dec.R
import com.twenty.dec.fragment.PerfilFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.test_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PostAdapter(
                    private val mContext: Context,
                    private val listPost: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>()
{

    private var firebaseUser : FirebaseUser? = null
    lateinit var itemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.PostViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.test_layout, parent, false)
        return PostViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listPost.size
    }

    inner class PostViewHolder (@NonNull itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

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

        var reportPost : ImageView = itemView.findViewById(R.id.report_post);

        var snackbarCoordinator : CoordinatorLayout = itemView.findViewById(R.id.coordinatorToSnackbar)

        init {
            itemView.placeHolder.setOnClickListener(this)
        }
        override fun onClick(p0: View) = itemClickListener.onItemClick(itemView,adapterPosition, listPost[position])
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, id: Post)
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = listPost[position]
        Picasso.get().load(post.getImage()).into(holder.image)
        holder.title.text = post.getTitle()
        holder.descriptionPost.text = post.getDescription()

        userInfo(holder.profileImage, holder.idUser, post.getUsername())
        isLike(post.getPid(), holder.likeButton)
        isJoined(post.getPid(), holder.joinButton)
        numberOfLikes(holder.countLike, post.getPid())
        numberOfComments(holder.countComen, post.getPid())

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Likes"){
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPid()).child(firebaseUser!!.uid).setValue(true)
                val snackbar: Snackbar = make(holder.likeButton, "liked "+ post.getTitle()+ "", Snackbar.LENGTH_SHORT)
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
               // snackbar.anchorView = holder.descriptionPost
                snackbar.show()

            }else{
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPid()).child(firebaseUser!!.uid).removeValue()
            }
        }

        holder.joinButton.setOnClickListener {

            if (holder.joinButton.tag == "Join"){
                FirebaseDatabase.getInstance().reference.child("Join").child(post.getPid()).child(firebaseUser!!.uid).setValue(true)
                val snackbar: Snackbar = make(holder.joinButton, "joined to "+ post.getTitle()+ "", Snackbar.LENGTH_SHORT)
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.show()
               //FirebaseDatabase.getInstance().reference.child("Join").child(firebaseUser!!.uid).child(post.getPid()).setValue(true)
            }else{
               FirebaseDatabase.getInstance().reference.child("Join").child(post.getPid()).child(firebaseUser!!.uid).removeValue()
               //FirebaseDatabase.getInstance().reference.child("Join").child(firebaseUser!!.uid).child(post.getPid()).removeValue()
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
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.content_fragment, PerfilFragment()).commit()
        }

        holder.reportPost.setOnClickListener {

            val items = arrayOf("Report", "Share it")

            val builder = MaterialAlertDialogBuilder(mContext)
                builder.setMessage("Do you want to Report this post?")
                builder.setPositiveButton("OK"){
                    dialogInterface, i ->

                    val formato = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currenDate = formato.format(Date())
                    val reportpostRef = FirebaseDatabase.getInstance().reference.child("Report")
                    val reportpostMap = HashMap<String, Any>()
                    reportpostMap["reporter"] = firebaseUser!!.uid;
                    reportpostMap["userReported"] = post.getUsername()
                    reportpostMap["date"] = currenDate
                    reportpostMap["post"] = post.getPid()

                    reportpostRef.push().setValue(reportpostMap)
                }
                builder.setNegativeButton("Cancel", null)
                val dialogInterface = builder.create()
                dialogInterface.show()

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

                    likeButton.setImageResource(R.drawable.ic_like_black)
                    likeButton.tag = "Likes"

                }
            }

        })
    }

    private fun isJoined(pid: String, joinButton : ImageView){

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likesRef = FirebaseDatabase.getInstance().reference.child("Join").child(pid)
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){

                    joinButton.setImageResource(R.drawable.joined)
                    joinButton.tag = "Joined"

                }else{

                    joinButton.setImageResource(R.drawable.join)
                    joinButton.tag = "Join"

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
