package com.project.tex.onboarding.api

import com.project.tex.onboarding.model.*
import io.reactivex.Single
import retrofit2.http.*

interface RetrofitService {
    @POST("auth/login")
    fun loginUser(@Body params: LoginBody): Single<LoginResponse>

    @POST("users/isUsernameAvailable")
    fun checkUsernameAvailability(@Body params: CheckUsernameBody): Single<CheckUsernameResponse>

    @POST("users/register")
    fun register(@Body params: RegisterBody): Single<RegisterResponse>

    @POST("users/otpVerify")
    fun verifyOtp(@Body params: LoginBody): Single<LoginResponse>

    @POST("users/resendOtp")
    fun resendOtp(@Body params: LoginBody): Single<LoginResponse>

    @PUT("users/enduser/{username}")
    fun updateUser(@Path("username") username: String, @Body params: UpdateUserBody, @Header("Authorization") authorization:String): Single<UpdateUserResponse>
}