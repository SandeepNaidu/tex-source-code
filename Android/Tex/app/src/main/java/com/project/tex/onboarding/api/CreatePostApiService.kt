package com.project.tex.onboarding.api

import com.project.tex.aritist_profile.model.PostsBody
import com.project.tex.main.model.*
import com.project.tex.onboarding.model.*
import com.project.tex.post.model.AllPostData
import com.project.tex.post.model.PostData
import io.reactivex.Single
import retrofit2.http.*
import retrofit2.http.Body

interface CreatePostApiService {
    @GET("posts2/getAllWithCount/{id}")
    fun getAllPosts(
        @Path("id")
        id: Int,
        @Header("Authorization") authorization: String
    ): Single<AllPostData>

    @GET("posts2/getAllByArtistId/{id}")
    fun getAllSavedPosts(
        @Path("id")
        id: Int,
        @Header("Authorization") authorization: String
    ): Single<AllPostData>

    @POST("posts2")
    fun createPost(
        @Body params: PostData,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @POST("posts2/save")
    fun callSavePost(
        @Body saveAvidBody: PostReactionBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @POST("posts2/like")
    fun callLikePost(
        @Body params: PostReactionBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @POST("posts2/share")
    fun callSharePost(
        @Body params: PostReactionBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @POST("posts2/unsave")
    fun callUnSavePost(
        @Body saveAvidBody: PostReactionBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @POST("posts2/unlike")
    fun callUnLikePost(
        @Body params: PostReactionBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @POST("posts2/vote")
    fun callNewVotePost(
        @Body params: NewVoteBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @POST("posts2/report")
    fun callReportPost(
        @Body params: PostReportBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @PUT("posts2/{PostId}")
    fun editPost(
        @Path("avidId") avidId: String,
        @Body params: EditAvidBody, @Header("Authorization") authorization: String
    ): Single<AllPostData>

    @GET("avids/mode/public")
    fun getAllAvids(@Header("Authorization") authorization: String): Single<AvidListResponse>

}