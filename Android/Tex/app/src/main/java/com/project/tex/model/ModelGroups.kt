package com.project.tex.model

class ModelGroups {
    var groupId: String? = null
    var gName: String? = null
    var gUsername: String? = null
    var gIcon: String? = null

    constructor() {}
    constructor(groupId: String?, gName: String?, gUsername: String?, gIcon: String?) {
        this.groupId = groupId
        this.gName = gName
        this.gUsername = gUsername
        this.gIcon = gIcon
    }

    fun getgName(): String? {
        return gName
    }

    fun setgName(gName: String?) {
        this.gName = gName
    }

    fun getgUsername(): String? {
        return gUsername
    }

    fun setgUsername(gUsername: String?) {
        this.gUsername = gUsername
    }

    fun getgIcon(): String? {
        return gIcon
    }

    fun setgIcon(gIcon: String?) {
        this.gIcon = gIcon
    }
}