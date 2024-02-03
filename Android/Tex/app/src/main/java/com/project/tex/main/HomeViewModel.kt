package com.project.tex.main

import com.project.tex.base.BaseViewModel
import com.project.tex.base.data.SharedPrefsKey
import com.project.tex.onboarding.OnboardRepository

class HomeViewModel() : BaseViewModel() {
    private val userRepository: OnboardRepository by lazy {
        OnboardRepository
    }

    fun getProfileImage(): String {
        return userRepository.dataKeyValue.getValueString(SharedPrefsKey.USER_PROFILE_IMG) ?: ""
    }

    fun getUserId(): Int {
        return userRepository.dataKeyValue.getValueInt(SharedPrefsKey.USER_ID)
    }
}