package com.project.tex.model

class ModelLive {
    var room: String? = null
    var userid: String? = null

    constructor() {}
    constructor(room: String?, userid: String?) {
        this.room = room
        this.userid = userid
    }
}