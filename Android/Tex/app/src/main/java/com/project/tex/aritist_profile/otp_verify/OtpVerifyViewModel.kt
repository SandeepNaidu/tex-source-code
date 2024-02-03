package com.project.tex.onboarding.otp

import androidx.lifecycle.MutableLiveData
import com.project.tex.aritist_profile.ProfileRepository
import com.project.tex.base.BaseViewModel
import com.project.tex.onboarding.OnboardRepository
import com.project.tex.onboarding.model.LoginResponse

class OtpVerifyViewModel : BaseViewModel() {
    private val profileRepository: ProfileRepository by lazy {
        ProfileRepository
    }



}