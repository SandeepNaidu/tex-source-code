package com.project.tex.model

class ModelPost {
    var id: String? = null
    var pId: String? = null
    var text: String? = null
    var type: String? = null
    var meme: String? = null
    var vine: String? = null
    var pTime: String? = null

    constructor() {}
    constructor(
        id: String?,
        pId: String?,
        text: String?,
        type: String?,
        meme: String?,
        vine: String?,
        pTime: String?
    ) {
        this.id = id
        this.pId = pId
        this.text = text
        this.type = type
        this.meme = meme
        this.vine = vine
        this.pTime = pTime
    }

    fun getpId(): String? {
        return pId
    }

    fun setpId(pId: String?) {
        this.pId = pId
    }

    fun getpTime(): String? {
        return pTime
    }

    fun setpTime(pTime: String?) {
        this.pTime = pTime
    }
}