package com.project.tex.main.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PostApiResponse(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("responseCode")
    val responseCode: Int?
) {
    @Keep
    data class Body(
        @SerializedName("post")
        val post: Post?
    ) {
        @Keep
        data class Post(
            @SerializedName("body")
            val body: Body?,
            @SerializedName("resultcode")
            val resultcode: String?
        ) {
            @Keep
            data class Body(
                @SerializedName("message")
                val message: String?
            )
        }
    }
}