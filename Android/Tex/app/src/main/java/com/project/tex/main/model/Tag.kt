package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("label")
    val label: String?,
    @SerializedName("value")
    val value: Int?,
    @SerializedName("isActive")
    val isActive: Int?
) : java.io.Serializable