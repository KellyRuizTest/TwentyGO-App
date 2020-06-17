package com.example.encounter.Model

import android.media.audiofx.AudioEffect

class Post {

    private var date : String = ""
    private var description : String = ""
    private var image : String = ""
    private var pid : String = ""
    private var title : String = ""
    private var username : String = ""

    constructor()
    constructor(
        date: String,
        description: String,
        image: String,
        pid: String,
        title: String,
        username: String

    ) {
        this.date = date
        this.description = description
        this.image = image
        this.pid = pid
        this.title = title
        this.username = username
    }

    fun getPid() : String { return pid }
    fun getTitle() : String { return title }
    fun getImage() : String { return image }
    fun getDescription() : String { return description }
    fun getUsername() : String { return username }
    fun getDate() : String { return date }

}