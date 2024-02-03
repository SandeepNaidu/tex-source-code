package com.project.tex.main.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PostReportBody(
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
        val artistId: Int?,
        @SerializedName("postId")
        val postId: Int?
    )
}