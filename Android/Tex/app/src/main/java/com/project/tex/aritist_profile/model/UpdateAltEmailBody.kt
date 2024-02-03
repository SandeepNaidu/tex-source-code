package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class UpdateAltEmailBody(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("alternateEmail")
    val alternateEmail: String?
)