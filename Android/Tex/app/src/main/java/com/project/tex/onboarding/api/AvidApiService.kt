package com.project.tex.onboarding.api

import com.project.tex.main.model.*
import com.project.tex.onboarding.model.*
import io.reactivex.Single
import retrofit2.http.*
import retrofit2.http.Body

interface AvidApiService {
    @GET("avids/mode/public")
    fun getAllAvids(@Header("Authorization") authorization: String): Single<AvidListResponse>

    @GET("avids/{avidId}/artistId/{userId}")
    fun getAvidsDetailForArtist(
        @Path("userId") userId: Int,
        @Path("avidId") avidId: Int,
        @Header("Authorization") authorization: String
    ): Single<AvidDetailsResponse>

    @POST("avids/save")
    fun callSaveAvid(
        @Body saveAvidBody: SaveAvidBody,
        @Header("Authorization") authorization: String
    ): Single<LikeShareSaveResponse>

    @POST("avids/like")
    fun callLikeAvid(
        @Body params: LikeAvidBody,
        @Header("Authorization") authorization: String
    ): Single<LikeShareSaveResponse>

    @POST("avids/share")
    fun callShareAvid(
        @Body params: ShareAvidBody,
        @Header("Authorization") authorization: String
    ): Single<LikeShareSaveResponse>

    @PUT("avids/{avidId}")
    fun editAvid(
        @Path("avidId") avidId: String,
        @Body params: EditAvidBody, @Header("Authorization") authorization: String
    ): Single<Any>

    @POST("avids")
    fun createPost(
        @Body params: CreateAvidMainBody,
        @Header("Authorization") authorization: String
    ): Single<LikeShareSaveResponse>

    @GET("tags/name/{name}")
    fun getTagByName(
        @Path("name") name: String,
        @Header("Authorization") authorization: String
    ): Single<TagsListResponse>

    @GET("tags/name/{ids}")
    fun getTagById(
        @Path("ids") ids: IntArray,
        @Header("Authorization") authorization: String
    ): Single<TagsListResponse>

    @GET("tags")
    fun getAllTag(
        @Header("Authorization") authorization: String
    ): Single<TagsListResponse>

}