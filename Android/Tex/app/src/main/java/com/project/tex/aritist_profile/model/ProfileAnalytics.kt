package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProfileAnalytics(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("responseCode")
    val responseCode: Int?
) {
    @Keep
    data class Body(
        @SerializedName("post")
        val post: List<Post?>?
    ) {
        @Keep
        data class Post(
            @SerializedName("reactionCount")
            val reactionCount: Int?,
            @SerializedName("searchCount")
            val searchCount: Int?
        )
    }
}