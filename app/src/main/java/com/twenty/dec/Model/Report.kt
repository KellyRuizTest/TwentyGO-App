package com.twenty.dec.Model

class Report {

    private var date : String = ""
    private var post : String = ""
    private var reporter : String = ""
    private var userReported : String = ""

    constructor()

    constructor(date: String, post: String, reporter: String, userReported: String) {
        this.date = date
        this.post = post
        this.reporter = reporter
        this.userReported = userReported
    }

    fun getDate() : String {
        return date
    }

    fun getPost() : String {
        return post
    }

    fun getReporter() : String{
        return reporter
    }

    fun getUserReported() : String {
        return userReported
    }

    fun setDate(date : String){this.date = date}
    fun setPost(post: String){this.post = post}
    fun setReporter(reporter: String){this.reporter = reporter}
    fun setUserReported(reported : String){userReported = reported }

}