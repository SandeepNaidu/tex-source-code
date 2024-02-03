package com.project.tex.model

class ModelStory {
    @kotlin.jvm.JvmField
    var imageUri: String? = null
    var timestart: Long = 0
    var timeend: Long = 0
    @kotlin.jvm.JvmField
    var storyid: String? = null
    var userid: String? = null

    constructor(
        imageUri: String?,
        timestart: Long,
        timeend: Long,
        storyid: String?,
        userid: String?
    ) {
        this.imageUri = imageUri
        this.timestart = timestart
        this.timeend = timeend
        this.storyid = storyid
        this.userid = userid
    }

    constructor() {}
}