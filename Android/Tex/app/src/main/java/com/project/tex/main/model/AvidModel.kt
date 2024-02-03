package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class AvidModel(
    @SerializedName("body")
    val body: BodyX?,
    @SerializedName("resultcode")
    val resultcode: String?
) : java.io.Serializable {
    override fun toString(): String {
        return "Avid(body=$body, resultcode=$resultcode)"
    }
}