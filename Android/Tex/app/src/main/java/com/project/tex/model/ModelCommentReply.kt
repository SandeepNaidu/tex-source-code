package com.project.tex.model

class ModelCommentReply {
    var cId: String? = null
    var comment: String? = null
    var timestamp: String? = null
    var id: String? = null
    var rId: String? = null

    constructor() {}
    constructor(cId: String?, comment: String?, timestamp: String?, id: String?, rId: String?) {
        this.cId = cId
        this.comment = comment
        this.timestamp = timestamp
        this.id = id
        this.rId = rId
    }

    fun getcId(): String? {
        return cId
    }

    fun setcId(cId: String?) {
        this.cId = cId
    }

    fun getrId(): String? {
        return rId
    }

    fun setrId(rId: String?) {
        this.rId = rId
    }
}