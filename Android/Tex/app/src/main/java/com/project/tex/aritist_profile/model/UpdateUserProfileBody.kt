package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class UpdateUserProfileBody(
    @SerializedName("age")
    val age: String? = null,
    @SerializedName("bio")
    val bio: String? = null,
    @SerializedName("contactNumber")
    val contactNumber: String? = null,
    @SerializedName("dob")
    val dob: String? = null,
    @SerializedName("email")
    val email: String?=null,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("height")
    val height: String? = null,
    @SerializedName("id")
    val id: Int,
    @SerializedName("alternateContact")
    val alternateContact: String? = null,
    @SerializedName("alternateEmail")
    val alternateEmail: String? = null,
    @SerializedName("isVisible")
    val isVisible: Int? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("location")
    val location: String? = null,
//    @SerializedName("notes")
//    val notes: String?,
    @SerializedName("profileImage")
    val profileImage: String? = null
)