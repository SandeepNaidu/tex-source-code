package com.project.tex.aritist_profile

import androidx.core.text.isDigitsOnly
import com.project.tex.GlobalApplication
import com.project.tex.aritist_profile.model.*
import com.project.tex.base.data.SharedPreference
import com.project.tex.base.network.ApiClient
import com.project.tex.main.model.AvidByArtistListResponse
import com.project.tex.main.model.AvidData
import com.project.tex.main.model.AvidListResponse
import com.project.tex.main.model.PostApiResponse
import com.project.tex.post.CreatePostRepository
import com.project.tex.post.model.AllPostData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.streams.toList


object ProfileRepository {
    private val apiClient by lazy {
        ApiClient.profileApiService
    }
    val dataKeyValue: SharedPreference by lazy {
        SharedPreference(GlobalApplication.instance)
    }

    fun getAllMediaPostList(artistId: Int): Single<MutableList<Any>> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.getAllPosts(artistId, auth).flatMap {
            val listOfPost: MutableList<Any> = mutableListOf()
            it.body?.posts?.body?.data?.let { posts ->
                posts.document?.forEach { p ->
                    /*if (!StringUtils.equals(
                            p?.artistFirstName,
                            "Super"
                        )
                    )*/
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
//                posts.poll?.forEach { p ->
//                    /*if (!StringUtils.equals(
//                            p?.artistFirstName,
//                            "Super"
//                        )
//                    )*/ if (p != null) {
//                    p.createAt = p.createAt?.plus("000")
//                    listOfPost.add(p)
//                    p.date = p.createAt?.plus("000")
//                }
//                }
//                posts.music?.forEach { p ->
//                    /*if (!StringUtils.equals(
//                            p?.artistFirstName,
//                            "Super"
//                        )
//                    )*/ if (p != null) {
//                    p.createAt = p.createAt?.plus("000")
//                    listOfPost.add(p)
//                    p.date = p.createAt
//                }
//                }
                posts.video?.forEach { p ->
                    /*if (!StringUtils.equals(
                            p?.artistFirstName,
                            "Super"
                        )
                    )*/
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.image?.forEach { p ->
                    /*if (!StringUtils.equals(
                            p?.artistFirstName,
                            "Super"
                        )
                    )*/
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
            }
            Single.just(listOfPost.stream().sorted { o1, o2 ->
                try {
                    (o2 as? AllPostData.Body.Posts.DateComparable)?.date?.let { it1 ->
                        (o1 as? AllPostData.Body.Posts.DateComparable)?.date?.compareTo(
                            it1
                        )
                    } ?: 0
                } catch (e: Exception) {
                    0
                }
            }.toList())
        }.map { t ->
            val mutable = mutableListOf<Any>()
            mutable.addAll(t.reversed())
            return@map mutable
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllAvidListing(
        userId: Int
    ): Single<AvidByArtistListResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
//        val auth= "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoic3VwZXJhZG1pbiIsImN1c3RvbTpyb2xlIjoxLCJlbWFpbCI6InNyaW5pQGdtYWlsLmNvbSIsImlhdCI6MTY3MjgyMjMyMywiZXhwIjoxNjc1NDE0MzIzfQ.v2D2vIgGtv5_-6Lob8XNguap8JuHOAn4ZikL2uUjKns"
        return apiClient.getAllAvids(auth, userId).flatMap {
            val latestBased = AvidByArtistListResponse(
                responseCode = it.responseCode,
                body = AvidByArtistListResponse.Body(it.body?.avid?.reversed())
            )
            Single.just(latestBased)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun createPortfolio(
        userId: Int,
        fileUrl: String,
        thumbUrl: String,
        type: String,
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val body = PortfolioBody(
            artistId = userId,
            userId = userId,
            description = "Portfolio - $type",
            imageUrl = fileUrl,
            thumbUrl = thumbUrl,
            portfolioType = type,
            title = "Portfolio Title",
            videoUrl = fileUrl
        )
        return apiClient.createPortfolio(body, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllPortfolios(userId: Int): Single<PortfolioListResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.getAllPortfolio(userId, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun createExperience(
        userId: Int,
        companyName: String,
        description: String,
        jobTitle: String,
        startDate: String,
        endDate: String,
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val body = ExperienceBody(
            data = ExperienceBody.Data(
                companyName = companyName,
                description = description,
                startDate = startDate,
                endDate = endDate,
                jobTitle = jobTitle,
                userId = userId.toString()
            )
        )
        return apiClient.createExperience(body, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateExperience(
        exp: Int,
        companyName: String,
        description: String,
        jobTitle: String,
        startDate: String,
        endDate: String,
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val body = UpdateExperienceBody(
            data = UpdateExperienceBody.Data(
                companyName = companyName,
                description = description,
                startDate = startDate,
                endDate = endDate,
                jobTitle = jobTitle,
            )
        )
        return apiClient.updateExperience(exp, body, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllExperience(userId: Int): Single<ExperienceListResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.getExperience(userId, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getProfileData(userId: Int): Single<UserProfileData> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.getUserProfileData(userId, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateProfileContactData(
        userId: Int, contact: String, email: String, alternateMobile: String, alternateEmail: String
    ): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val body = UpdateUserProfileBody(
            id = userId,
            contactNumber = contact,
            email = email,
            alternateContact = alternateMobile,
            alternateEmail = alternateEmail
        )
        return apiClient.updateProfile(body, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadProfileImage(
        userId: Int, profileImage: String
    ): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val body = UploadImageBody(
            id = userId,
            profileImage = profileImage
        )
        return apiClient.uploadProfileImage(body, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun markUserAsFresher(
        userId: Int, fresher: Int
    ): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val body = MarkUserFresher(
            id = userId,
            isFresher = fresher
        )
        return apiClient.markUserAsFresher(body, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateProfileData(
        userId: Int,
        firstName: String,
        lastName: String,
        bio: String,
        gender: String,
        dob: String,
        age: String,
        height: String,
        city: String,
        profileImage: String
    ): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val body = UpdateUserProfileBody(
            age = age,
            bio = bio,
            dob = dob,
            firstName = firstName,
            lastName = lastName,
            gender = gender,
            height = height,
            id = userId,
            profileImage = profileImage,
            location = city,
        )
        return apiClient.updateProfile(body, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getProfileAnalytics(artistId: Int): Single<ProfileAnalytics> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.getProfileAnalyticsData(artistId, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateVisibility(eId: Int, visible: Int): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.updateContactVisibility(UpdateVisibility(eId, visible), auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateProfileImageLastUpdatedOn(
        artistId: Int,
        visible: String
    ): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.updateProfileImageLastUpdatedOn(
            UpdateProfileImageLastUpdate(
                artistId,
                visible
            ), auth
        ).subscribeOn(Schedulers.io())
    }

    fun updateContactLastUpdatedOn(
        artistId: Int,
        visible: String
    ): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.updateContactLastUpdatedOn(
            UpdateContactLastUpdate(
                artistId,
                visible
            ), auth
        ).subscribeOn(Schedulers.io())
    }

    fun updateStatusSetting(
        artistId: Int,
        statusAvailability: String,
        onlineStatus: String
    ): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.updateStatusSetting(
            UpdateStatusSetting(
                artistId,
                onlineStatus = onlineStatus,
                statusOfAvailability = statusAvailability
            ), auth
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateEmail(eId: Int, data: String): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.updateEmail(UpdateEmailBody(eId, data), auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateMob(eId: Int, data: String): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.updateContact(UpdateContactBody(eId, data), auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateAltEmail(eId: Int, data: String): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.updateAltEmail(UpdateAltEmailBody(eId, data), auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun updateAltMob(eId: Int, data: String): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.updateAltMobile(UpdateAltMobBody(eId, data), auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getProfileActionData(artistId: Int, userId: Int): Single<ProfileAnalyticsData> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.getProfileActionData(artistId, userId, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun saveProfile(artistId: Int, myUserId: Int, isSave: Int): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.saveProfile(
            ProfileSaveody(
                data = ProfileSaveody.Data(
                    artistId = artistId.toString(),
                    savedBy = myUserId.toString(),
                    isSave = isSave
                )
            ), auth
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun likeProfile(artistId: Int, myUserId: Int, islike: Int): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.likeProfile(
            ProfileLikeBody(
                data = ProfileLikeBody.Data(
                    artistId = artistId.toString(),
                    islike = islike,
                    likedBy = myUserId.toString()
                )
            ), auth
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun shareProfile(artistId: Int, myUserId: Int): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.shareProfile(
            ProfileShareBody(
                data = ProfileShareBody.Data(
                    artistId = artistId.toString(),
                    sharedBy = myUserId.toString()
                )
            ), auth
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun viewProfile(artistId: String, myUserId: String): Single<GenericUpdateResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.viewProfile(
            ProfileViewBody(
                data = ProfileViewBody.Data(
                    artistId = artistId,
                    viewedBy = myUserId
                )
            ), auth
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}