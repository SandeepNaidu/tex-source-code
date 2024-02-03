package com.project.tex.model

class ModelVerification {
    var name: String? = null
    var username: String? = null
    var vId: String? = null
    var uID: String? = null
    var link: String? = null
    var known: String? = null

    constructor() {}
    constructor(
        name: String?,
        username: String?,
        vId: String?,
        uID: String?,
        link: String?,
        known: String?
    ) {
        this.name = name
        this.username = username
        this.vId = vId
        this.uID = uID
        this.link = link
        this.known = known
    }

    fun getvId(): String? {
        return vId
    }

    fun setvId(vId: String?) {
        this.vId = vId
    }

    fun getuID(): String? {
        return uID
    }

    fun setuID(uID: String?) {
        this.uID = uID
    }
}