package com.project.tex.onboarding.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LoginBody(
    @SerializedName("data")
    val data: Data,
    @SerializedName("auditLog")
    val auditLog: AuditLog
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginBody

        if (data != other.data) return false
        if (auditLog != other.auditLog) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + auditLog.hashCode()
        return result
    }
}

data class Data(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("token")
    val token: String = "",
    @SerializedName("isMobile")
    val isMobile: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Data

        if (username != other.username) return false
        if (password != other.password) return false
        if (token != other.token) return false
        if (isMobile != other.isMobile) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + isMobile.hashCode()
        return result
    }
}

data class AuditLog(
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("activity")
    val activity: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("page")
    val page: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuditLog

        if (userId != other.userId) return false
        if (activity != other.activity) return false
        if (description != other.description) return false
        if (page != other.page) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + activity.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + page.hashCode()
        return result
    }
}
