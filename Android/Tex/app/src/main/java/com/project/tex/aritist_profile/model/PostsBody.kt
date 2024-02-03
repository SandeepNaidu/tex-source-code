package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PostsBody(
    @SerializedName("userId")
    val userId: Int?
)