package com.project.tex.onboarding.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CheckUsernameBody(
    @SerializedName("data")
    val data: Data,
    @SerializedName("auditLog")
    val auditLog: AuditLog
) {
    @Keep
    data class Data(
        @SerializedName("username")
        val username: String,
    )
}
