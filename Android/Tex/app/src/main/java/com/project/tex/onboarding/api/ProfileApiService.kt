package com.project.tex.onboarding.api

import com.project.tex.aritist_profile.model.*
import com.project.tex.main.model.*
import com.project.tex.onboarding.model.*
import com.project.tex.post.model.AllPostData
import com.project.tex.post.model.PostListData
import io.reactivex.Single
import retrofit2.http.*
import retrofit2.http.Body

interface ProfileApiService {
    @GET("posts2/getAllByArtistId/{id}")
    fun getAllPosts(
        @Path("id")
        id: Int,
        @Header("Authorization") authorization: String
    ): Single<AllPostData>

    @PUT("posts2/{PostId}")
    fun editPost(
        @Path("avidId") avidId: String,
        @Body params: EditAvidBody, @Header("Authorization") authorization: String
    ): Single<AllPostData>

    @GET("avids/artist/{userId}")
    fun getAllAvids(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Int
    ): Single<AvidByArtistListResponse>

    @POST("portfolio")
    fun createPortfolio(
        @Body params: PortfolioBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @PUT("portfolio/{pId}")
    fun updatePortfolio(
        @Body params: PortfolioBody,
        @Path("pId") pId: Int,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>


    @DELETE("portfolio/{pId}")
    fun deletePortfolio(
        @Path("pId") pId: Int,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @GET("portfolio/user/{pId}")
    fun getAllPortfolio(
        @Path("pId") pId: Int,
        @Header("Authorization") authorization: String
    ): Single<PortfolioListResponse>

    @POST("portfolio/experience")
    fun createExperience(
        @Body params: ExperienceBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @GET("portfolio/experience/user/{pId}")
    fun getExperience(
        @Path("pId") pId: Int,
        @Header("Authorization") authorization: String
    ): Single<ExperienceListResponse>

    @PUT("portfolio/experience/{expId}")
    fun updateExperience(
        @Path("expId") expId: Int,
        @Body params: UpdateExperienceBody,
        @Header("Authorization") authorization: String
    ): Single<PostApiResponse>

    @GET("users/getById/{pid}")
    fun getUserProfileData(
        @Path("pid") pid: Int,
        @Header("Authorization") authorization: String
    ): Single<UserProfileData>

    @PUT("users/updateProfile/update")
    fun updateProfile(
        @Body params: UpdateUserProfileBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/uploadImage/upload")
    fun uploadProfileImage(
        @Body params: UploadImageBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/markUserisFresher/update")
    fun markUserAsFresher(
        @Body params: MarkUserFresher,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @GET("users/profile/count/artist/{artistId}/by/{uId}")
    fun getProfileActionData(
        @Path("artistId") artistId: Int,
        @Path("uId") userId: Int,
        @Header("Authorization") authorization: String
    ): Single<ProfileAnalyticsData>

    @GET("posts2/reaction/artist/{pid}")
    fun getProfileAnalyticsData(
        @Path("pid") pid: Int,
        @Header("Authorization") authorization: String
    ): Single<ProfileAnalytics>

    @PUT("users/updateProfile/update")
    fun updateContactVisibility(
        @Body param : UpdateVisibility,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/updateProfile/update")
    fun updateStatusSetting(
        @Body param : UpdateStatusSetting,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/updateProfile/update")
    fun updateProfileImageLastUpdatedOn(
        @Body param : UpdateProfileImageLastUpdate,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/updateProfile/update")
    fun updateContactLastUpdatedOn(
        @Body param : UpdateContactLastUpdate,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/updateProfile/update")
    fun updateContact(
        @Body param : UpdateContactBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/updateProfile/update")
    fun updateEmail(
        @Body param : UpdateEmailBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/updateProfile/update")
    fun updateAltEmail(
        @Body param : UpdateAltEmailBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @PUT("users/updateProfile/update")
    fun updateAltMobile(
        @Body param : UpdateAltMobBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @POST("users/profile/save")
    fun saveProfile(
        @Body params: ProfileSaveody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @POST("users/profile/share")
    fun shareProfile(
        @Body params: ProfileShareBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @POST("users/profile/like")
    fun likeProfile(
        @Body params: ProfileLikeBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

    @POST("users/profile/view")
    fun viewProfile(
        @Body params: ProfileViewBody,
        @Header("Authorization") authorization: String
    ): Single<GenericUpdateResponse>

}