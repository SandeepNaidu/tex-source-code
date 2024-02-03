package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProfileAnalyticsData(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("responseCode")
    val responseCode: Int?
) {
    @Keep
    data class Body(
        @SerializedName("profile")
        val profile: List<Profile?>?
    ) {
        @Keep
        data class Profile(
            @SerializedName("likeCount")
            val likeCount: Int?,
            @SerializedName("saveCount")
            val saveCount: Int?,
            @SerializedName("shareCount")
            val shareCount: Int?,
            @SerializedName("viewCount")
            val viewCount: Int?,
            @SerializedName("isProfileLiked")
            val isProfileLiked: Int?,
            @SerializedName("isProfileSaved")
            val isProfileSaved: Int?,
            @SerializedName("isProfileShared")
            val isProfileShared: Int?,

        )
    }
}