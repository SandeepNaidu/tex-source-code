package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class BodyX(
    @SerializedName("message")
    val message: String?
) : java.io.Serializable {
    override fun toString(): String {
        return "BodyX(message=$message)"
    }
}