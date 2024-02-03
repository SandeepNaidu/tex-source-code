package com.project.tex.model

class ModelNotification {
    var pId: String? = null
    var timestamp: String? = null
    var pUid: String? = null
    var notification: String? = null
    var sUid: String? = null

    constructor() {}
    constructor(
        pId: String?,
        timestamp: String?,
        pUid: String?,
        notification: String?,
        sUid: String?
    ) {
        this.pId = pId
        this.timestamp = timestamp
        this.pUid = pUid
        this.notification = notification
        this.sUid = sUid
    }

    fun getpId(): String? {
        return pId
    }

    fun setpId(pId: String?) {
        this.pId = pId
    }

    fun getpUid(): String? {
        return pUid
    }

    fun setpUid(pUid: String?) {
        this.pUid = pUid
    }

    fun getsUid(): String? {
        return sUid
    }

    fun setsUid(sUid: String?) {
        this.sUid = sUid
    }
}