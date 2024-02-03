package com.project.tex.notifications

class Sender {
    private var data: Data? = null
    var to: String? = null

    constructor() {}
    constructor(data: Data?, to: String?) {
        this.data = data
        this.to = to
    }

    fun getData(): Data? {
        return data
    }

    fun setData(data: Data?) {
        this.data = data
    }
}