package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PortfolioListResponse(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("responseCode")
    val responseCode: Int?
) {
    @Keep
    data class Body(
        @SerializedName("portfolios")
        val portfolios: Portfolios?
    ) {
        @Keep
        data class Portfolios(
            @SerializedName("data")
            val `data`: List<Data?>?
        ) {
            @Keep
            data class Data(
                @SerializedName("artistId")
                val artistId: Int?,
                @SerializedName("createAt")
                val createAt: String?,
                @SerializedName("description")
                val description: String?,
                @SerializedName("id")
                val id: Int?,
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
        }
    }
}