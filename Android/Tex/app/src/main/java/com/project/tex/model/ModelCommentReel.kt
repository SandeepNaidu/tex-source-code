package com.project.tex.model

class ModelCommentReel {
    var cId: String? = null
    var comment: String? = null
    var timestamp: String? = null
    var id: String? = null
    var pId: String? = null

    constructor() {}
    constructor(cId: String?, comment: String?, timestamp: String?, id: String?, pId: String?) {
        this.cId = cId
        this.comment = comment
        this.timestamp = timestamp
        this.id = id
        this.pId = pId
    }

    fun getcId(): String? {
        return cId
    }

    fun setcId(cId: String?) {
        this.cId = cId
    }

    fun getpId(): String? {
        return pId
    }

    fun setpId(pId: String?) {
        this.pId = pId
    }
}