package com.project.tex.model

class ModelPostGroup {
    var id: String? = null
    var group: String? = null
    var pId: String? = null
    var text: String? = null
    var type: String? = null
    var meme: String? = null
    var vine: String? = null
    var pTime: String? = null

    constructor() {}
    constructor(
        id: String?,
        group: String?,
        pId: String?,
        text: String?,
        type: String?,
        meme: String?,
        vine: String?,
        pTime: String?
    ) {
        this.id = id
        this.group = group
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