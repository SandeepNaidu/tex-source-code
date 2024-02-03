package com.project.tex.onboarding.otp

import androidx.lifecycle.MutableLiveData
import com.project.tex.base.BaseViewModel
import com.project.tex.onboarding.OnboardRepository
import com.project.tex.onboarding.model.LoginResponse

class OtpViewModel : BaseViewModel() {
    private val userRepository: OnboardRepository by lazy {
        OnboardRepository
    }
    private val userIdMutableLive = MutableLiveData<String>()

    val userIdLD = userIdMutableLive

    fun setUserId(uid: String) {
        userIdMutableLive.value = uid
    }

    fun loginUser(username: String, password: String): MutableLiveData<LoginResponse> {
        return userRepository.login(username, password)
    }

}