package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class DataShare(
    @SerializedName("artistId")
    val artistId: String?,
    @SerializedName("avidId")
    val avidId: String?,
    @SerializedName("isShare")
    val isShare: Int?
)