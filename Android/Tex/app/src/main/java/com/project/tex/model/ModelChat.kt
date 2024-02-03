package com.project.tex.model

class ModelChat {
    var sender: String? = null
    var receiver: String? = null
    var msg: String? = null
    var type: String? = null
    var timestamp: String? = null
    var isSeen = false

    constructor(
        sender: String?,
        receiver: String?,
        msg: String?,
        type: String?,
        isSeen: Boolean,
        timestamp: String?
    ) {
        this.sender = sender
        this.receiver = receiver
        this.msg = msg
        this.type = type
        this.isSeen = isSeen
        this.timestamp = timestamp
    }

    constructor() {}
}