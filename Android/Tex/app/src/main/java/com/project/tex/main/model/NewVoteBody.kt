package com.project.tex.main.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NewVoteBody(
    @SerializedName("artistId")
    val artistId: Int?,
    @SerializedName("optionId")
    val optionId: Int?,
    @SerializedName("postId")
    val postId: Int?
)