package com.project.tex.notifications

class Data {
    var user: String? = null
    var body: String? = null
    var title: String? = null
    var sent: String? = null
    var type: String? = null
    var icon: Int? = null

    constructor() {}
    constructor(
        user: String?,
        body: String?,
        title: String?,
        sent: String?,
        type: String?,
        icon: Int?
    ) {
        this.user = user
        this.body = body
        this.title = title
        this.sent = sent
        this.type = type
        this.icon = icon
    }
}