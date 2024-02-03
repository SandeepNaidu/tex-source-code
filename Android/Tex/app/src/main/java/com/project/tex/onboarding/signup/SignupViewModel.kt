package com.project.tex.onboarding.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.tex.base.BaseViewModel
import com.project.tex.onboarding.OnboardRepository
import com.project.tex.onboarding.model.LoginResponse

class SignupViewModel() : BaseViewModel() {
    private val userRepository: OnboardRepository by lazy {
        OnboardRepository
    }
    private val userIdMutableLive = MutableLiveData<String>("")
    private val userTypeMutableLive = MutableLiveData<String>("")
    private val userNameMutableLive = MutableLiveData<String>("")
    private val genderMutableLive = MutableLiveData<String>("")
    private val dobMutableLive = MutableLiveData<String>("")
    private val ageMutableLive = MutableLiveData<Int>(0)
    private val isUserIdEmailType = MutableLiveData<Boolean>(false)

    val userIdLD: LiveData<String> = userIdMutableLive
    val userTypeLD: LiveData<String> = userTypeMutableLive
    val userNameLD: LiveData<String> = userNameMutableLive
    val genderLD: LiveData<String> = genderMutableLive
    val dobLD: LiveData<String> = dobMutableLive
    val ageLD: LiveData<Int> = ageMutableLive
    val isUserIdEmailTypeLD : LiveData<Boolean> = isUserIdEmailType

    fun setUserId(uid: String) {
        userIdMutableLive.value = uid
    }

    fun setUsertype(type: String) {
        userTypeMutableLive.value = type
    }

    fun setUserName(name: String) {
        userNameMutableLive.value = name
    }

    fun setGender(gender: String) {
        genderMutableLive.value = gender
    }

    fun setAge(age: Int) {
        ageMutableLive.value = age
    }

    fun setDob(dob: String) {
        dobMutableLive.value = dob
    }

    fun setIsUserIdEmailType(isSo: Boolean) {
        isUserIdEmailType.value = isSo
    }

    fun loginUser(userName: String, pass: String): MutableLiveData<LoginResponse> {
        return userRepository.login(userName, pass)
    }

}