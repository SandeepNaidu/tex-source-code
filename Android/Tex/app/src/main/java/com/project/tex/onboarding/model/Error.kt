package com.project.tex.onboarding.model

import androidx.annotation.Keep

@Keep
data class Error(
    val debugDescription: String,
    val errorCode: Int,
    val errorMessage: String,
    val errorUIMessage: String
)