package com.project.tex.onboarding.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CheckUsernameResponse(
    @SerializedName("responseCode")
    val responseCode: Int,
    @SerializedName("body")
    val body: CheckUsernameResponseBody?
) {
    @Keep
    data class CheckUsernameResponseBody(
        @SerializedName("isAvailable")
        val isAvailable: ResultCode,
        @SerializedName("errors")
        val errors: List<Error?>?
    )
    @Keep
    data class ResultCode(
        @SerializedName("resultcode")
        val resultcode: String?,
        @SerializedName("body")
        val body: Body?
    )
    @Keep
    data class Body(
        @SerializedName("isAvailable")
        val isAvailable: Boolean?,
    )

}
