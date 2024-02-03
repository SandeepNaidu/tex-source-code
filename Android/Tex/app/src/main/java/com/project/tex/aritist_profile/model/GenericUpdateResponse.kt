package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GenericUpdateResponse(
    @SerializedName("body")
    val body: Body?=null,
    @SerializedName("responseCode")
    val responseCode: Int?=null
) {
    @Keep
    data class Body(
        @SerializedName("message")
        val message: String?=null
    )
}