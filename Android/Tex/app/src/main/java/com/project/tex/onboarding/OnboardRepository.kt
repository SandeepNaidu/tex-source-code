package com.project.tex.onboarding

import androidx.lifecycle.MutableLiveData
import com.project.tex.GlobalApplication
import com.project.tex.base.data.SharedPreference
import com.project.tex.base.network.ApiClient
import com.project.tex.onboarding.model.*
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


object OnboardRepository {
    private val token = ""
    val dataKeyValue: SharedPreference by lazy {
        SharedPreference(GlobalApplication.instance)
    }
    fun checkUserExists(
        username: String,
        mutableLiveData: MutableLiveData<CheckUsernameResponse>
    ): MutableLiveData<CheckUsernameResponse> {
        val body = CheckUsernameBody(
            CheckUsernameBody.Data(username),
            AuditLog(
                username,
                "checkUsernameAvailable",
                "checkUsernameAvailable: $username",
                "Login screen"
            )
        )
        ApiClient.apiService.checkUsernameAvailability(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<CheckUsernameResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: CheckUsernameResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = CheckUsernameResponse(400, null)
                }
            })

        return mutableLiveData
    }

    fun login(username: String, password: String): MutableLiveData<LoginResponse> {

        val mutableLiveData = MutableLiveData<LoginResponse>()
        val data = Data(username, password)
        val auditLog = AuditLog(username, "Login", "Login: $username", "Login")
        val body = LoginBody(data, auditLog)

        ApiClient.apiService.loginUser(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<LoginResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: LoginResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value =
                        LoginResponse(
                            responseCode = (e as? HttpException)?.code() ?: 400,
                            body = LoginResponse.Body()
                        )
                }
            })

        return mutableLiveData
    }

    fun verifyOtp(): MutableLiveData<LoginResponse> {

        val mutableLiveData = MutableLiveData<LoginResponse>()

        val body = LoginBody(
            Data("superadmin", "sad", token),
            AuditLog("superadmin", "Login", "Login: superadmin", "Homescreen")
        )
        ApiClient.apiService.verifyOtp(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<LoginResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: LoginResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = LoginResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }

    fun register(
        username: String,
        fullName: String,
        userType: Int,
        isUsernameEmail: Boolean,
        mutableLiveData: MutableLiveData<RegisterResponse>
    ): MutableLiveData<RegisterResponse> {

        val name = fullName.trim().contains(" ")
        val body = RegisterBody(
            RegisterBody.Data(username = username, password = "0000").also
            {
                if (name) {
                    it.firstName = fullName.substringBefore(" ")
                    it.lastName = fullName.substringAfter(" ")
                } else {
                    it.firstName = fullName
                }
                if (isUsernameEmail) {
                    it.email = username
                } else {
                    it.contactNumber = username
                }
                it.roleId = userType
            },
            AuditLog(username, "register", "register: $username", "register flow")
        )
        ApiClient.apiService.register(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<RegisterResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: RegisterResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = RegisterResponse(400, null)
                }
            })

        return mutableLiveData
    }

    fun resendOtp(): MutableLiveData<LoginResponse> {

        val mutableLiveData = MutableLiveData<LoginResponse>()

        val body = LoginBody(
            Data("superadmin", "40cb344ad09b447b84d5b76bb7d79415:595385d68e4cd3f27f1050cb", token),
            AuditLog("superadmin", "Login", "Login: superadmin", "Homescreen")
        )
        ApiClient.apiService.resendOtp(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<LoginResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: LoginResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = LoginResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }

    fun updateUser(
        auth: String,
        username: String,
        gender: String? = null,
        dob: String? = null,
        age: Int? = null,
        contactNumber: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        mutableLiveData: MutableLiveData<UpdateUserResponse>
    ): MutableLiveData<UpdateUserResponse> {
        val body = UpdateUserBody(
            UpdateUserBody.Data(
                gender = gender,
                dob = dob,
                firstName = firstName,
                lastName = lastName,
                contactNumber = contactNumber,
                age = age
            ),
            AuditLog("User", "User", "Update User", "Register Screen")
        )
        ApiClient.apiService.updateUser(username, body, auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<UpdateUserResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: UpdateUserResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = UpdateUserResponse(400, null)
                }
            })

        return mutableLiveData
    }
}