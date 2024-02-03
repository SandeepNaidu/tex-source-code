package com.project.tex.aritist_profile.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProfileShareBody(
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
        @SerializedName("sharedBy")
        val sharedBy: String?
    )
}