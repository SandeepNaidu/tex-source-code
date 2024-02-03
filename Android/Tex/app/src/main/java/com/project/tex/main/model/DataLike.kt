package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class DataLike(
    @SerializedName("artistId")
    val artistId: String?,
    @SerializedName("avidId")
    val avidId: String?,
    @SerializedName("islike")
    val isLike: Int?
)