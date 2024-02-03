package com.project.tex.main.model


import com.google.gson.annotations.SerializedName
import com.project.tex.onboarding.model.AuditLog

data class EditAvidBody(
    @SerializedName("auditLog")
    val auditLog: AuditLog? = AuditLog(),
    @SerializedName("data")
    val `data`: DataX?
)