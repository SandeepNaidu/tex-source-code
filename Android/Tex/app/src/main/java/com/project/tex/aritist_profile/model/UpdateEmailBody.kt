package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class UpdateEmailBody(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("email")
    val email: String?
)