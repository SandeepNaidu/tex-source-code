package com.project.tex.model

class ModelProduct {
    var id: String? = null
    var pId: String? = null
    var title: String? = null
    var price: String? = null
    var des: String? = null
    var location: String? = null
    var cat: String? = null
    var type: String? = null
    var photo: String? = null

    constructor() {}
    constructor(
        id: String?,
        pId: String?,
        title: String?,
        price: String?,
        des: String?,
        location: String?,
        cat: String?,
        type: String?,
        photo: String?
    ) {
        this.id = id
        this.pId = pId
        this.title = title
        this.price = price
        this.des = des
        this.location = location
        this.cat = cat
        this.type = type
        this.photo = photo
    }

    fun getpId(): String? {
        return pId
    }

    fun setpId(pId: String?) {
        this.pId = pId
    }
}