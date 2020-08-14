package com.encounter.twenty.Model

class Users {

    private var name : String = ""
    private var email : String = ""
    private var image : String = ""
    private var pid : String = ""
    private var bio : String = ""
    private var username : String = ""
    private var password : String = ""

    constructor()

    constructor(name : String, email : String, image : String, pid : String, bio : String, username : String, password : String){
            this.name = name
            this.email = email
            this.image = image
            this.pid = pid
            this.bio = bio
            this.username = username
            this.password = password }

    fun getName() : String { return name }
    fun getUsername() : String {return username}
    fun getEmail() : String {return email}
    fun getPassword() : String{return password}
    fun getBio() : String{ return bio}
    fun getImage() : String {return image}
    fun getPid() : String {return pid}

    fun setName(foo : String) {name = foo}
    fun setUsername(foo : String) {username = foo}
    fun setEmail(foo : String) {email = foo}
    fun setBio(foo : String) {bio = foo}
    fun setPassword(foo : String) {password = foo}
    fun setImage(foo : String) {image = foo}

}