package com.project.tex.onboarding.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UpdateUserBody(
    @SerializedName("data")
    val data: Data,
    @SerializedName("auditLog")
    val auditLog: AuditLog
) {
    @Keep
    data class Data(
        @SerializedName("firstName")
        var firstName: String? = "",
        @SerializedName("lastName")
        var lastName: String? = "",
        @SerializedName("contactNumber")
        var contactNumber: String? = "",
        @SerializedName("gender")
        val gender: String? = "",
        @SerializedName("age")
        val age: Int? = 0,
        @SerializedName("dob")
        val dob: String? = "",
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Data

            if (firstName != other.firstName) return false
            if (lastName != other.lastName) return false
            if (contactNumber != other.contactNumber) return false
            if (gender != other.gender) return false
            if (age != other.age) return false
            if (dob != other.dob) return false

            return true
        }

        override fun hashCode(): Int {
            var result = firstName.hashCode()
            result = 31 * result + lastName.hashCode()
            result = 31 * result + contactNumber.hashCode()
            result = 31 * result + gender.hashCode()
            result = 31 * result + (age ?: 0)
            result = 31 * result + dob.hashCode()
            return result
        }

        override fun toString(): String {
            return "Data(firstName='$firstName', lastName='$lastName', contactNumber='$contactNumber', gender='$gender', age=$age, dob='$dob')"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateUserBody

        if (data != other.data) return false
        if (auditLog != other.auditLog) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + auditLog.hashCode()
        return result
    }
}
