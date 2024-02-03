package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProfileSaveody(
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
        @SerializedName("isSave")
        val isSave: Int?,
        @SerializedName("savedBy")
        val savedBy: String?
    )
}