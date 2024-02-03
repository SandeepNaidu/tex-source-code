package com.project.tex.firebase.model

import com.google.gson.annotations.SerializedName

data class TUser(
    @SerializedName("bio")
    val bio: String? = "",
    @SerializedName("email")
    val email: String? = "",
    @SerializedName("id")
    val id: String?,
    @SerializedName("link")
    val link: String? = "",
    @SerializedName("location")
    val location: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("phone")
    val phone: String? = "",
    @SerializedName("photo")
    val photo: String? = "",
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("typingTo")
    val typingTo: String? = "",
    @SerializedName("username")
    val username: String? = "",
    @SerializedName("verified")
    val verified: String? = "",
    @SerializedName("userType")
    val userType: String? = ""
) : java.io.Serializable