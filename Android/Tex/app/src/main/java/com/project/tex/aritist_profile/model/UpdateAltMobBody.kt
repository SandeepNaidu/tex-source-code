package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class UpdateAltMobBody(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("alternateContact")
    val alternateContact: String?
)