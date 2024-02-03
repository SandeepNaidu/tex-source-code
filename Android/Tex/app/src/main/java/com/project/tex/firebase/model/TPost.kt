package com.project.tex.firebase.model

import com.google.gson.annotations.SerializedName

data class TPost(
    @SerializedName("id")
    val id: String,
    @SerializedName("pId")
    val pId: String? = "",
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: String? = "",
    @SerializedName("meme")
    val meme: String,
    @SerializedName("vine")
    val vine: String? = "",
    @SerializedName("pTime")
    val pTime: String,
    @SerializedName("loc")
    val loc: String? = "",
)
