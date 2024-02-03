package com.project.tex.onboarding.signup.fragments.dob

import androidx.lifecycle.MutableLiveData
import com.project.tex.base.BaseViewModel
import com.project.tex.onboarding.OnboardRepository
import com.project.tex.onboarding.model.UpdateUserResponse

class DOBInputViewModel : BaseViewModel() {
    private val userRepository: OnboardRepository by lazy {
        OnboardRepository
    }

    private val updateUserMutableLive = MutableLiveData<UpdateUserResponse>()
    val registerLD = updateUserMutableLive

    fun updateUser(
        auth: String,
        dob: String? = null,
        age: Int? = null,
        contactNumber: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        gender: String? = null, username: String
    ): MutableLiveData<UpdateUserResponse> {
        return userRepository.updateUser(
            auth = auth,
            dob = dob,
            gender = gender,
            age = age,
            contactNumber = contactNumber,
            firstName = firstName,
            lastName = lastName,
            mutableLiveData = updateUserMutableLive,
            username = username
        )
    }
}