package com.twenty.veinte.Model

class Comments {

    private var comment : String = ""
    private var commenter : String = ""


    constructor()

    constructor(comment: String, commenter: String) {
        this.comment = comment
        this.commenter = commenter
    }

    fun getComment() : String{
        return comment
    }

    fun getCommenter() : String{
        return commenter
    }

    fun setComment(comment: String) { this.comment = comment}
    fun setCommenter(commentador: String) { this.commenter = commentador}
}