package com.project.tex.aritist_profile.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class UserProfileData(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("responseCode")
    val responseCode: Int?
) {
    @Keep
    data class Body(
        @SerializedName("users")
        val users: Users?
    ) {
        @Keep
        data class Users(
            @SerializedName("body")
            val body: List<Body?>?,
            @SerializedName("resultcode")
            val resultcode: String?
        ) {
            @Keep
            data class Body(
                @SerializedName("age")
                val age: String?,
                @SerializedName("alternateContact")
                var alternateContact: String?,
                @SerializedName("alternateEmail")
                var alternateEmail: String?,
                @SerializedName("bio")
                val bio: String?,
                @SerializedName("contactNumber")
                var contactNumber: String?,
                @SerializedName("createdBy")
                val createdBy: String?,
                @SerializedName("createdOn")
                val createdOn: String?,
                @SerializedName("creationDate")
                val creationDate: String?,
                @SerializedName("dob")
                val dob: String?,
                @SerializedName("email")
                var email: String?,
                @SerializedName("firstName")
                val firstName: String?,
                @SerializedName("gender")
                val gender: String?,
                @SerializedName("height")
                val height: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("image")
                val image: String?,
                @SerializedName("isArtist")
                val isArtist: Int?,
                @SerializedName("isFresher")
                val isFresher: String?,
                @SerializedName("isRecruiter")
                val isRecruiter: Int?,
                @SerializedName("isVisible")
                val isVisible: String?,
                @SerializedName("lastName")
                val lastName: String?,
                @SerializedName("lastUpdateOn")
                val lastUpdateOn: String?,
                @SerializedName("location")
                val location: String?,
                @SerializedName("notes")
                val notes: String?,
                @SerializedName("profileImage")
                val profileImage: String?,
                @SerializedName("roleId")
                val roleId: Int?,
                @SerializedName("updatedBy")
                val updatedBy: String?,
                @SerializedName("updationDate")
                val updationDate: String?,
                @SerializedName("username")
                val username: String?,
                @SerializedName("onlineStatus")
                var onlineStatus: String?,
                @SerializedName("statusOfAvailability")
                var statusOfAvailability: String?,
                @SerializedName("contactLastUpdatedOn")
                val contactLastUpdatedOn: String?,
                @SerializedName("profileImageLastUpdatedOn")
                val profileImageLastUpdatedOn: String?
            ): java.io.Serializable
        }
    }
}