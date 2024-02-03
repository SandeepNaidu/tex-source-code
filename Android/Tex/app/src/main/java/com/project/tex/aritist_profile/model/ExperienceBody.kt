package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ExperienceBody(
    @SerializedName("auditLog")
    val auditLog: AuditLog? = AuditLog(),
    @SerializedName("data")
    val `data`: Data?
) {
    @Keep
    class AuditLog

    @Keep
    data class Data(
        @SerializedName("companyName")
        val companyName: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("endDate")
        val endDate: String?,
        @SerializedName("jobTitle")
        val jobTitle: String?,
        @SerializedName("startDate")
        val startDate: String?,
        @SerializedName("userId")
        val userId: String?
    )
}