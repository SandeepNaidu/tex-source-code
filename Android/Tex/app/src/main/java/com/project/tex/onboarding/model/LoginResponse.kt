package com.project.tex.onboarding.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LoginResponse(
    @SerializedName("body")
    val body: Body? = null,
    @SerializedName("responseCode")
    val responseCode: Int? = null
) {
    @Keep
    data class Body(
        @SerializedName("idToken")
        val idToken: String? = null,
        @SerializedName("refreshToken")
        val refreshToken: String? = null,
        @SerializedName("user")
        val user: User? = null,
        @SerializedName("errors")
        val errors: List<Error>? = null
    )

    @Keep
    data class User(
        @SerializedName("contactNumber")
        val contactNumber: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("firstName")
        val firstName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("lastName")
        val lastName: String?,
        @SerializedName("roleId")
        val roleId: Int?,
        @SerializedName("roleType")
        val roleType: String?,
        @SerializedName("username")
        val username: String?
    ) : java.io.Serializable
}