package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("artistId")
    val artistId: String?,
    @SerializedName("avidId")
    val avidId: String?,
    @SerializedName("isSave")
    val isSave: Int?
)