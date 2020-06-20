package com.example.encounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.encounter.Adapters.PostAdapter
import com.example.encounter.Model.Post

class EncounterActivity : AppCompatActivity() {

    private var postAdapter : PostAdapter? = null
    private var postList : MutableList<Post>? = null
    private var followingList : MutableList<Post>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encounter)

    }
}