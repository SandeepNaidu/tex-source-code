package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class LikeShareSaveResponse(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("responseCode")
    val responseCode: Int?
) : java.io.Serializable {
    override fun toString(): String {
        return "LikeShareSaveResponse(body=$body, responseCode=$responseCode)"
    }
}