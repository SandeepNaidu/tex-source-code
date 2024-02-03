package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class UpdateStatusSetting(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("onlineStatus")
    val onlineStatus: String?,
    @SerializedName("statusOfAvailability")
    val statusOfAvailability: String?
)