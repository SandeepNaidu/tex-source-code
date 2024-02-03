package com.project.tex.model

class ModelHigh {
    @kotlin.jvm.JvmField
    var uri: String? = null
    @kotlin.jvm.JvmField
    var type: String? = null
    var storyid: String? = null
    var userid: String? = null

    constructor() {}
    constructor(uri: String?, type: String?, storyid: String?, userid: String?) {
        this.uri = uri
        this.type = type
        this.storyid = storyid
        this.userid = userid
    }
}