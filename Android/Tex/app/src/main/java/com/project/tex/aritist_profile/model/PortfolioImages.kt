package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PortfolioImages(
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
                @SerializedName("imageUrl")
                val imageUrl: String?
            )
        }
    }
}