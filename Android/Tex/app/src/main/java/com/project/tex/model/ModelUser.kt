package com.project.tex.model

class ModelUser {
    var name: String? = null
    var username: String? = null
    var location: String? = null
    var photo: String? = null
    var id: String? = null
    var verified: String? = null
    var typingTo: String? = null
    var isBlocked = false

    constructor() {}
    constructor(
        name: String?,
        username: String?,
        location: String?,
        photo: String?,
        id: String?,
        verified: String?,
        typingTo: String?,
        isBlocked: Boolean
    ) {
        this.name = name
        this.username = username
        this.location = location
        this.photo = photo
        this.id = id
        this.verified = verified
        this.typingTo = typingTo
        this.isBlocked = isBlocked
    }
}