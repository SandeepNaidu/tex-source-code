package com.project.tex.post.model


import com.google.gson.annotations.SerializedName
import com.project.tex.onboarding.model.AuditLog

data class CreatePostBody(
    @SerializedName("auditLog")
    val auditLog: AuditLog? = AuditLog(),
    @SerializedName("data")
    val pData: PostData?
) : java.io.Serializable