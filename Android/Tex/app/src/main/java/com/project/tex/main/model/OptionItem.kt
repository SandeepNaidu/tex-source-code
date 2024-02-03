package com.project.tex.main.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class OptionItem(
    @SerializedName("count")
    var count: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("optionText")
    val optionText: String?
)