package com.project.tex.aritist_profile.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

class CitiesList : ArrayList<CitiesList.CitiesListItem>(){
    @Keep
    data class CitiesListItem(
        @SerializedName("id")
        val id: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("state")
        val state: String?
    )
}