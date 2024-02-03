package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class UploadImageBody(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("profileImage")
    val profileImage: String?
)