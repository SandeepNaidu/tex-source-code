package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("caption")
    val caption: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("coverContent")
    val coverContent: String?,
    @SerializedName("hashTag")
    val hashTag: String?,
    @SerializedName("mode")
    val mode: String?,
    @SerializedName("title")
    val title: String?
)