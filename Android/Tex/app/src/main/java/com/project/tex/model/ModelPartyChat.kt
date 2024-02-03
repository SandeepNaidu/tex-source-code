package com.project.tex.model

class ModelPartyChat {
    var chatId: String? = null
    var userId: String? = null
    var msg: String? = null

    constructor() {}
    constructor(chatId: String?, userId: String?, msg: String?) {
        this.chatId = chatId
        this.userId = userId
        this.msg = msg
    }
}