package com.project.tex.onboarding.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RegisterResponse(
    @SerializedName("responseCode")
    val responseCode: Int,
    @SerializedName("body")
    val body: Body?
) {
    @Keep
    data class Body(
        @SerializedName("body")
        val body: String?,
        @SerializedName("errors")
        val errors: List<Error>?
    )
}