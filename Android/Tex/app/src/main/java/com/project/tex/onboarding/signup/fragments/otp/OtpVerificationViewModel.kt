package com.project.tex.onboarding.signup.fragments.otp

import androidx.lifecycle.MutableLiveData
import com.project.tex.base.BaseViewModel
import com.project.tex.onboarding.OnboardRepository
import com.project.tex.onboarding.model.LoginResponse
import com.project.tex.onboarding.model.RegisterResponse

class OtpVerificationViewModel : BaseViewModel() {
    private val userRepository: OnboardRepository by lazy {
        OnboardRepository
    }

    private val registerMutableLive = MutableLiveData<RegisterResponse>()
    val registerLD = registerMutableLive

    fun register(
        username: String,
        fullname: String,
        userType: Int,
        isEmail: Boolean
    ): MutableLiveData<RegisterResponse>? {
        return userRepository.register(username, fullname, userType, isEmail, registerMutableLive)
    }

    fun loginUser(userName: String, pass: String): MutableLiveData<LoginResponse> {
        return userRepository.login(userName, pass)
    }
}