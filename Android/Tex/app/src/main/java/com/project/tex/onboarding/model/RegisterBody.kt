package com.project.tex.onboarding.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RegisterBody(
    @SerializedName("data")
    val data: Data,
    @SerializedName("auditLog")
    val auditLog: AuditLog
) {
    @Keep
    data class Data(
        @SerializedName("username")
        val username: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("firstName")
        var firstName: String = "",
        @SerializedName("lastName")
        var lastName: String = "",
        @SerializedName("email")
        var email: String = "",
        @SerializedName("contactNumber")
        var contactNumber: String = "",
        @SerializedName("roleId")
        var roleId: Int = 121,
        @SerializedName("gender")
        val gender: String = "",
        @SerializedName("age")
        val age: Int = 0,
        @SerializedName("dob")
        val dob: String = "",
        @SerializedName("selectedGroups")
        val selectedGroups: IntArray = intArrayOf(),
        @SerializedName("selectedPolicies")
        val selectedPolicies: IntArray = intArrayOf(),
        @SerializedName("notes")
        val notes: String = ""
//        @SerializedName("isMobile")
//        val isMobile: Boolean = true
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Data

            if (username != other.username) return false
            if (password != other.password) return false
            if (firstName != other.firstName) return false
            if (lastName != other.lastName) return false
            if (email != other.email) return false
            if (contactNumber != other.contactNumber) return false
            if (roleId != other.roleId) return false
            if (gender != other.gender) return false
            if (age != other.age) return false
            if (dob != other.dob) return false
            if (!selectedGroups.contentEquals(other.selectedGroups)) return false
            if (!selectedPolicies.contentEquals(other.selectedPolicies)) return false
            if (notes != other.notes) return false

            return true
        }

        override fun hashCode(): Int {
            var result = username.hashCode()
            result = 31 * result + password.hashCode()
            result = 31 * result + firstName.hashCode()
            result = 31 * result + lastName.hashCode()
            result = 31 * result + email.hashCode()
            result = 31 * result + contactNumber.hashCode()
            result = 31 * result + roleId
            result = 31 * result + gender.hashCode()
            result = 31 * result + age
            result = 31 * result + dob.hashCode()
            result = 31 * result + selectedGroups.contentHashCode()
            result = 31 * result + selectedPolicies.contentHashCode()
            result = 31 * result + notes.hashCode()
            return result
        }
    }
}
