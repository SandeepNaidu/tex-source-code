package com.project.tex.aritist_profile.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProfileViewBody(
    @SerializedName("auditLog")
    val auditLog: AuditLog? = AuditLog(),
    @SerializedName("data")
    val `data`: Data?
) {
    @Keep
    class AuditLog

    @Keep
    data class Data(
        @SerializedName("artistId")
        val artistId: String?,
        @SerializedName("viewedBy")
        val viewedBy: String?
    )
}