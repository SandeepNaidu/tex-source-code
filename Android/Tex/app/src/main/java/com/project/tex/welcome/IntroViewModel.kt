package com.project.tex.welcome

import androidx.lifecycle.ViewModel
import com.project.tex.onboarding.OnboardRepository

class IntroViewModel() : ViewModel() {
    private val userRepository: OnboardRepository by lazy {
        OnboardRepository
    }

}