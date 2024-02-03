package com.project.tex.model

class ModelReel(
    var id: String? = null,
    var pId: String? = null,
    var text: String? = null,
    var video: String? = null,
    var privacy: String? = null,
    var pTime: String? = null
) {
    var comment: String? = null

    init {
        this.comment = comment
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