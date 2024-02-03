package com.project.tex.post

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.project.tex.GlobalApplication
import com.project.tex.base.data.SharedPreference
import com.project.tex.base.network.ApiClient
import com.project.tex.db.AppDatabase
import com.project.tex.main.HomeRepository
import com.project.tex.main.model.*
import com.project.tex.post.model.AllPostData
import com.project.tex.post.model.PostData
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.streams.toList


object CreatePostRepository {
    private val apiClient by lazy {
        ApiClient.createPostApiService
    }
    val dataKeyValue: SharedPreference by lazy {
        SharedPreference(GlobalApplication.instance)
    }

    val db = Room.databaseBuilder(
        GlobalApplication.instance, AppDatabase::class.java, "tex_db"
    ).build()

    fun getAllPostList(artistId: Int): Single<MutableList<Any>> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.getAllPosts(artistId, auth).flatMap {
            val listOfPost: MutableList<Any> = mutableListOf()
            it.body?.posts?.body?.data?.let { posts ->
                posts.event?.forEach { p ->
//                    if (!StringUtils.equals(
//                            p?.artistFirstName,
//                            "Super"
//                        )
//                    )
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.document?.forEach { p ->
//                    if (!StringUtils.equals(
//                            p?.artistFirstName,
//                            "Super"
//                        )
//                    )
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.poll?.forEach { p ->
//                    if (!StringUtils.equals(
//                            p?.artistFirstName,
//                            "Super"
//                        )
//                    )
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.music?.forEach { p ->
//                    if (!StringUtils.equals(
//                            p?.artistFirstName,
//                            "Super"
//                        )
//                    )
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.video?.forEach { p ->
//                    if (!StringUtils.equals(
//                            p?.artistFirstName,
//                            "Super"
//                        )
//                    )
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.image?.forEach { p ->
//                    if (!StringUtils.equals(
//                            p?.artistFirstName,
//                            "Super"
//                        )
//                    )
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
            if (mutable.size > 1) {
                mutable.add(1, mutableListOf<AvidData>())
            } else {
                mutable.add(mutableListOf<AvidData>())
            }
            return@map mutable
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllSavedPostList(artistId: Int): Single<MutableList<Any>> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.getAllSavedPosts(artistId, auth).flatMap {
            val listOfPost: MutableList<Any> = mutableListOf()
            it.body?.posts?.body?.data?.let { posts ->
                posts.event?.forEach { p ->
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.document?.forEach { p ->
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.poll?.forEach { p ->
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.music?.forEach { p ->
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.video?.forEach { p ->
                    if (p?.createAt != null && p.createAt?.isDigitsOnly() == true)
                        if (p != null) {
                            p.createAt = p.createAt?.plus("000")
                            listOfPost.add(p)
                            p.date = p.createAt
                        }
                }
                posts.image?.forEach { p ->
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
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllAvidListing(
    ): Single<AvidListResponse> {
        val auth = HomeRepository.dataKeyValue.getValueString("refreshToken")!!
//        val auth= "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoic3VwZXJhZG1pbiIsImN1c3RvbTpyb2xlIjoxLCJlbWFpbCI6InNyaW5pQGdtYWlsLmNvbSIsImlhdCI6MTY3MjgyMjMyMywiZXhwIjoxNjc1NDE0MzIzfQ.v2D2vIgGtv5_-6Lob8XNguap8JuHOAn4ZikL2uUjKns"
        return apiClient.getAllAvids(auth).flatMap {
            val latestBased = AvidListResponse(
                responseCode = it.responseCode,
                body = AvidListResponse.Body(it.body?.avids?.reversed())
            )
            Single.just(latestBased)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    fun savePostCall(
        postSaveBody: PostReactionBody
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.callSavePost(postSaveBody, auth).subscribeOn(Schedulers.io())
    }

    fun unsavePostCall(
        postSaveBody: PostReactionBody
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.callUnSavePost(postSaveBody, auth).subscribeOn(Schedulers.io())
    }

    fun sharePostCall(
        postShareBody: PostReactionBody
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.callSharePost(postShareBody, auth).subscribeOn(Schedulers.io())

    }

    fun unlikePostCall(
        postLikeBody: PostReactionBody
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.callUnLikePost(postLikeBody, auth).subscribeOn(Schedulers.io())
    }

    fun likePostCall(
        postLikeBody: PostReactionBody
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.callLikePost(postLikeBody, auth).subscribeOn(Schedulers.io())
    }

    fun votePostCall(
        postVoteBody: NewVoteBody
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.callNewVotePost(postVoteBody, auth).subscribeOn(Schedulers.io())
    }

    fun reportPostCall(
        postReportBody: PostReportBody
    ): Single<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        return apiClient.callReportPost(postReportBody, auth).subscribeOn(Schedulers.io())
    }

    fun createPost(
        thumbUrl: String,
        postType: String,
        contentUrl: String,
        caption: String,
        hashTags: String,
        latLong: String,
        address: String,
        mutableLiveData: MutableLiveData<PostApiResponse>
    ): MutableLiveData<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val artistid = dataKeyValue.getValueInt("userId")
        val post = PostData(
            artistId = artistid.toString(),
            postType = postType,
            caption = caption,
            hashTag = hashTags,
            latlong = latLong,
            address = address
        )
        post.thumbUrl = thumbUrl
        when (postType) {
            "Photo" -> {
                post.imageUrl = contentUrl
                post.postType = "Image"
            }
            "Video" -> {
                post.videoUrl = contentUrl
            }
            "Music" -> {
                post.audioUrl = contentUrl
            }
            "Document" -> {
                post.documentUrl = contentUrl
            }
        }
        apiClient.createPost(post, auth)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PostApiResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: PostApiResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = PostApiResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }


    fun createEvent(
        description: String,
        eventType: String,
        eventFormat: String,
        event: String,
        eventTime: String,
        eventExternalLink: String,
        latLong: String,
        url: String,
        address: String,
        mutableLiveData: MutableLiveData<PostApiResponse>
    ): MutableLiveData<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val artistid = dataKeyValue.getValueInt("userId")
        val post = PostData(
            artistId = artistid.toString(),
            eventType = eventType,
            eventFormat = eventFormat,
            event = event,
            eventDateTime = eventTime,
            eventExternalLink = eventExternalLink,
            description = description,
            latlong = latLong,
            address = address,
            eventImageUrl = url,
            imageUrl = url
        )
        post.postType = "Event"
        apiClient.createPost(post, auth)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PostApiResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: PostApiResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = PostApiResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }

    fun createPoll(
        question: String,
        options: Array<String>,
        duration: Int = 7,
        latLong: String,
        address: String,
        mutableLiveData: MutableLiveData<PostApiResponse>
    ): MutableLiveData<PostApiResponse> {
        val auth = dataKeyValue.getValueString("refreshToken")!!
        val artistid = dataKeyValue.getValueInt("userId")
        val post = PostData(
            artistId = artistid.toString(),
            options = options,
            duration = duration,
            question = question,
            latlong = latLong,
            address = address
        )
        post.postType = "Poll"
        apiClient.createPost(post, auth)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PostApiResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: PostApiResponse) {
                    mutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    mutableLiveData.value = PostApiResponse(responseCode = 400, body = null)
                }
            })

        return mutableLiveData
    }
}