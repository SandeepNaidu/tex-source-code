package com.project.tex.main

import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.project.tex.GlobalApplication
import com.project.tex.base.data.SharedPreference
import com.project.tex.base.network.ApiClient
import com.project.tex.db.AppDatabase
import com.project.tex.main.model.*
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


object HomeRepository {
    private val apiClient by lazy {
        ApiClient.avidApiService
    }
    val dataKeyValue: SharedPreference by lazy {
        SharedPreference(GlobalApplication.instance)
    }

    val db = Room.databaseBuilder(
        GlobalApplication.instance, AppDatabase::class.java, "tex_db"
    ).build()

    fun getAllAvidListing(
        mutableLiveData: MutableLiveData<AvidListResponse>
    ): MutableLiveData<AvidListResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
//        val auth= "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoic3VwZXJhZG1pbiIsImN1c3RvbTpyb2xlIjoxLCJlbWFpbCI6InNyaW5pQGdtYWlsLmNvbSIsImlhdCI6MTY3MjgyMjMyMywiZXhwIjoxNjc1NDE0MzIzfQ.v2D2vIgGtv5_-6Lob8XNguap8JuHOAn4ZikL2uUjKns"
        apiClient.getAllAvids(auth).flatMap {
                val latestBased = AvidListResponse(
                    responseCode = it.responseCode,
                    body = AvidListResponse.Body(it.body?.avids?.reversed())
                )
                Single.just(latestBased)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<AvidListResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: AvidListResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = AvidListResponse(responseCode = 400)
                }
            })

        return mutableLiveData
    }

    fun getAvidDetail(
        avidId: Int, mutableLiveData: MutableLiveData<AvidDetailsResponse>
    ): MutableLiveData<AvidDetailsResponse> {
        val userId = dataKeyValue.getValueInt("userId")
        val auth = dataKeyValue.getValueString("refreshToken")!!
        apiClient.getAvidsDetailForArtist(userId, avidId, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<AvidDetailsResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: AvidDetailsResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = AvidDetailsResponse(responseCode = 400)
                }
            })

        return mutableLiveData
    }

    fun saveAvidCall(
        saveAvidBody: SaveAvidBody, mutableLiveData: MutableLiveData<LikeShareSaveResponse>
    ): MutableLiveData<LikeShareSaveResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
//        val auth =
//            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoic3VwZXJhZG1pbiIsImN1c3RvbTpyb2xlIjoxLCJlbWFpbCI6InNyaW5pQGdtYWlsLmNvbSIsImlhdCI6MTY3MjgyMjMyMywiZXhwIjoxNjc1NDE0MzIzfQ.v2D2vIgGtv5_-6Lob8XNguap8JuHOAn4ZikL2uUjKns"
        apiClient.callSaveAvid(saveAvidBody, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<LikeShareSaveResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: LikeShareSaveResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = LikeShareSaveResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }

    fun shareAvidCall(
        saveAvidBody: ShareAvidBody, mutableLiveData: MutableLiveData<LikeShareSaveResponse>
    ): MutableLiveData<LikeShareSaveResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
//        val auth =
//            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoic3VwZXJhZG1pbiIsImN1c3RvbTpyb2xlIjoxLCJlbWFpbCI6InNyaW5pQGdtYWlsLmNvbSIsImlhdCI6MTY3MjgyMjMyMywiZXhwIjoxNjc1NDE0MzIzfQ.v2D2vIgGtv5_-6Lob8XNguap8JuHOAn4ZikL2uUjKns"
        apiClient.callShareAvid(saveAvidBody, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<LikeShareSaveResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: LikeShareSaveResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = LikeShareSaveResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }

    fun likeAvidCall(
        saveAvidBody: LikeAvidBody, mutableLiveData: MutableLiveData<LikeShareSaveResponse>
    ): MutableLiveData<LikeShareSaveResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        apiClient.callLikeAvid(saveAvidBody, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<LikeShareSaveResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: LikeShareSaveResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = LikeShareSaveResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }

    fun createPost(
        createAvid: CreateAvidBody, mutableLiveData: MutableLiveData<LikeShareSaveResponse>
    ): MutableLiveData<LikeShareSaveResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val artistid = dataKeyValue.getValueInt("userId")
        createAvid.artistId = artistid
        apiClient.createPost(CreateAvidMainBody(post = createAvid), auth)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<LikeShareSaveResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: LikeShareSaveResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = LikeShareSaveResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }

    fun getAllTags(
        mutableLiveData: MutableLiveData<TagsListResponse>
    ): MutableLiveData<TagsListResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        apiClient.getAllTag(auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<TagsListResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: TagsListResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = TagsListResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }

    fun getTagsByName(
        name: String, mutableLiveData: MutableLiveData<TagsListResponse>
    ): MutableLiveData<TagsListResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        apiClient.getTagByName(name, auth).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<TagsListResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: TagsListResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = TagsListResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }
}