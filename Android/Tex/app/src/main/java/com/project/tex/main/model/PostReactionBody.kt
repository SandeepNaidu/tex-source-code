package com.project.tex.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PostReactionBody(
    @SerializedName("artistId")
    val artistId: Int?,
    @SerializedName("postId")
    val postId: Int?
)