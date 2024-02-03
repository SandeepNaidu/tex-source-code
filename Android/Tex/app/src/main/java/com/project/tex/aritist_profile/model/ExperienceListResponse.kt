package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ExperienceListResponse(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("responseCode")
    val responseCode: Int?
) {
    @Keep
    data class Body(
        @SerializedName("experiences")
        val experiences: List<Experience?>?
    ) {
        @Keep
        data class Experience(
            @SerializedName("companyName")
            val companyName: String?,
            @SerializedName("createAt")
            val createAt: String?,
            @SerializedName("description")
            val description: String?,
            @SerializedName("endDate")
            val endDate: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("jobTitle")
            val jobTitle: String?,
            @SerializedName("startDate")
            val startDate: String?,
            @SerializedName("userId")
            val userId: Int?
        ) : java.io.Serializable
    }
}