package com.project.tex.base.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.tex.BuildConfig
import com.project.tex.onboarding.api.AvidApiService
import com.project.tex.onboarding.api.CreatePostApiService
import com.project.tex.onboarding.api.ProfileApiService
import com.project.tex.onboarding.api.RetrofitService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.logging.Level

object ApiClient {

    private const val BASE_URL: String = "http://13.232.183.161:4200/tex/v1/"

    private val gson: Gson by lazy {
        GsonBuilder().setLenient().create()
    }

    private val httpClient: OkHttpClient.Builder by lazy {
        OkHttpClient.Builder()
    }

    private val retrofit: Retrofit by lazy {
        val levelType: HttpLoggingInterceptor.Level
        if (BuildConfig.DEBUG)
            levelType = HttpLoggingInterceptor.Level.BODY else levelType = HttpLoggingInterceptor.Level.NONE

        val logging = HttpLoggingInterceptor()
        logging.setLevel(levelType)

        httpClient.addInterceptor(logging)

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    val apiService: RetrofitService by lazy {
        retrofit.create(RetrofitService::class.java)
    }

    val avidApiService: AvidApiService by lazy {
        retrofit.create(AvidApiService::class.java)
    }

    val createPostApiService: CreatePostApiService by lazy {
        retrofit.create(CreatePostApiService::class.java)
    }

    val profileApiService: ProfileApiService by lazy {
        retrofit.create(ProfileApiService::class.java)
    }

}