package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("avid")
    val avid: AvidModel?
) : java.io.Serializable {
    override fun toString(): String {
        return "Body(avid=$avid)"
    }
}