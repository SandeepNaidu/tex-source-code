package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PortfolioBody(
    @SerializedName("artistId")
    val artistId: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("portfolioType")
    val portfolioType: String?,
    @SerializedName("thumbUrl")
    val thumbUrl: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("userId")
    val userId: Int?,
    @SerializedName("videoUrl")
    val videoUrl: String?
)