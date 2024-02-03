package com.project.tex.model

class ModelGroupChat {
    var sender: String? = null
    var msg: String? = null
    var type: String? = null
    var timestamp: String? = null

    constructor() {}
    constructor(sender: String?, msg: String?, type: String?, timestamp: String?) {
        this.sender = sender
        this.msg = msg
        this.type = type
        this.timestamp = timestamp
    }
}