package com.project.tex.onboarding.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.tex.onboarding.Constants
import com.project.tex.onboarding.OnboardRepository
import com.project.tex.onboarding.model.CheckUsernameResponse
import com.project.tex.onboarding.model.ErrorValidation
import com.project.tex.onboarding.model.LoginResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.TimeUnit

class QuickSigninViewModel : ViewModel() {
    private val subject: PublishSubject<String> = PublishSubject.create()
    private val subjectToShowError: PublishSubject<String> = PublishSubject.create()

    private val validationError = MutableLiveData<ErrorValidation>()
    val validationErrorLD = validationError

    private val compositeDisposable = CompositeDisposable()
    fun emit() {
        userIdLD.value?.let { s ->
//            if (isValidInput(s).isValid)
                subject.onNext(s)
//            else
                subjectToShowError.onNext(s)
        }
    }

    fun subscribeSubject() {
        compositeDisposable.add(subject.debounce(
            Constants.DEBOUNCE_INPUT_REQUEST_TIME,
            TimeUnit.MILLISECONDS
        )
            .switchMap { t ->
                if (isValidInput(t).isValid) {
                    Observable.just(t)
                } else {
                    Observable.empty()
                }
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                checkUserNamePresent()
            })

        compositeDisposable.add(subjectToShowError.debounce(
            Constants.DEBOUNCE_INPUT_VALIDATION_TIME,
            TimeUnit.MILLISECONDS
        )
            .switchMap { t ->
                if (isValidInput(t).isValid) {
                    Observable.empty()
                } else {
                    Observable.just(t)
                }
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe { s ->
                val isValid = isValidInput(s)
                validationError.value = isValid
            })
    }

    private fun isValidInput(s: String): ErrorValidation {
        var isValid = false
        if (android.util.Patterns.PHONE.matcher(s)
                .matches() && s.length == 10
        ) {
            isValid = true
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            isValid = true
        }

        return ErrorValidation(isValid, getInputType(s))
    }

    public fun getInputType(s: String): Int {
        var type = 0
        if (StringUtils.isNumeric(s)) {
            // means its a mobile number type else email type
            type = 1
        }
        return type
    }

    private val userRepository: OnboardRepository by lazy {
        OnboardRepository
    }

    private val userIdMutableLive = MutableLiveData<String>()
    val userIdLD = userIdMutableLive

    private val usernameAvailableMutableLive = MutableLiveData<CheckUsernameResponse>()
    val usernameAvailableLD = usernameAvailableMutableLive

    fun setUserId(uid: String) {
        userIdMutableLive.value = uid
    }

    private fun checkUserNamePresent(): MutableLiveData<CheckUsernameResponse>? {
        return userIdLD.value?.let {
            userRepository.checkUserExists(
                it,
                usernameAvailableMutableLive
            )
        }
    }

    fun checkSocialUserExist(email: String): MutableLiveData<CheckUsernameResponse>? {
        val ld = MutableLiveData<CheckUsernameResponse>()
        return userRepository.checkUserExists(email, ld)
    }

    fun loginUser(username: String, password: String): MutableLiveData<LoginResponse> {
        return userRepository.login(username, password)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}