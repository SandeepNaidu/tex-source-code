package com.project.tex.main.model

import com.google.gson.annotations.SerializedName

data class TagData(
    @SerializedName("name")
    val name: String?
) : java.io.Serializable