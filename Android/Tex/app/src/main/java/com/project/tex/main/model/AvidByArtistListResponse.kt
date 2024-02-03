package com.project.tex.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AvidByArtistListResponse(
    @SerializedName("body")
    val body: Body? = null,
    @SerializedName("responseCode")
    val responseCode: Int? = null
) {
    @Keep
    data class Body(
        @SerializedName("avid")
        val avid: List<AvidData>? = null
    )
}