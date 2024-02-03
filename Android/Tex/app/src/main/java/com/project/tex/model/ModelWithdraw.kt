package com.project.tex.model

class ModelWithdraw {
    var id: String? = null
    var amount: String? = null
    var way: String? = null
    var type: String? = null

    constructor() {}
    constructor(id: String?, amount: String?, way: String?, type: String?) {
        this.id = id
        this.amount = amount
        this.way = way
        this.type = type
    }
}