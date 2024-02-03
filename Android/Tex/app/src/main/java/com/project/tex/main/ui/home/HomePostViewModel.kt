package com.project.tex.main.ui.home

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.project.tex.base.BaseViewModel
import com.project.tex.main.model.*
import com.project.tex.post.CreatePostRepository
import com.project.tex.post.model.AllPostData
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class HomePostViewModel : BaseViewModel() {
    private val TAG: String = HomePostViewModel::class.java.simpleName
    private val postRepository: CreatePostRepository by lazy {
        CreatePostRepository
    }

    private val isScreenOff = MutableLiveData<Boolean>(false)
    public val isScreenOffObserver: LiveData<Boolean> = isScreenOff

    private val artistId: Int by lazy {
        postRepository.dataKeyValue.getValueInt("userId")
    }

    public fun getAllPostListing(): Single<MutableList<Any>> {
        return postRepository.getAllPostList(artistId).doOnSubscribe {
            compositeDisposable.add(it)
        }
    }

    public fun getAllSavedPostListing(): Single<MutableList<Any>> {
        return postRepository.getAllSavedPostList(artistId).doOnSubscribe {
            compositeDisposable.add(it)
        }
    }

    public fun getAvids(): Single<AvidListResponse> {
        return postRepository.getAllAvidListing()
    }

    public fun isScreenOff(): Boolean {
        return isScreenOff.value == true
    }

    fun likePost(postInfo: Any): Single<Any> {
        var isLiked = 0
        val id = when (postInfo) {
            is AllPostData.Body.Posts.Video -> {
                isLiked = if (postInfo.isLiked == 1) 0 else 1
                postInfo.id
            }
            is AllPostData.Body.Posts.Music -> {
                isLiked = if (postInfo.isLiked == 1) 0 else 1
                postInfo.id

            }
            is AllPostData.Body.Posts.Image -> {
                isLiked = if (postInfo.isLiked == 1) 0 else 1
                postInfo.id

            }
            is AllPostData.Body.Posts.Document -> {
                isLiked = if (postInfo.isLiked == 1) 0 else 1
                postInfo.id

            }
            is AllPostData.Body.Posts.Event -> {
                isLiked = if (postInfo.isLiked == 1) 0 else 1
                postInfo.id

            }
            is AllPostData.Body.Posts.Poll -> {
                isLiked = if (postInfo.isLiked == 1) 0 else 1
                postInfo.id

            }
            else -> {
                -1
            }
        }
        val body = PostReactionBody(
            artistId = artistId,
            postId = id
        )
        val aa = if (isLiked == 1) {
            postRepository.likePostCall(body)
        } else {
            postRepository.unlikePostCall(body)
        }
        return aa
            .flatMap {
                if (it.responseCode == 200) {
                    when (postInfo) {
                        is AllPostData.Body.Posts.Video -> {
                            postInfo.isLiked = isLiked
                            postInfo.likeCount =
                                (postInfo.likeCount ?: 0).plus(if (isLiked == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Music -> {
                            postInfo.isLiked = isLiked
                            postInfo.likeCount =
                                (postInfo.likeCount ?: 0).plus(if (isLiked == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Image -> {
                            postInfo.isLiked = isLiked
                            postInfo.likeCount =
                                (postInfo.likeCount ?: 0).plus(if (isLiked == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Document -> {
                            postInfo.isLiked = isLiked
                            postInfo.likeCount =
                                (postInfo.likeCount ?: 0).plus(if (isLiked == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Event -> {
                            postInfo.isLiked = isLiked
                            postInfo.likeCount =
                                (postInfo.likeCount ?: 0).plus(if (isLiked == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Poll -> {
                            postInfo.isLiked = isLiked
                            postInfo.likeCount =
                                (postInfo.likeCount ?: 0).plus(if (isLiked == 1) 1 else -1)
                        }
                    }
                    Single.just(postInfo)
                } else {
                    Single.error<Exception>(Exception(""))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun savePost(postInfo: Any): Single<Any> {
        var isSaved = 0
        val id = when (postInfo) {
            is AllPostData.Body.Posts.Video -> {
                isSaved = if (postInfo.isSaved == 1) 0 else 1
                postInfo.id
            }
            is AllPostData.Body.Posts.Music -> {
                isSaved = if (postInfo.isSaved == 1) 0 else 1
                postInfo.id

            }
            is AllPostData.Body.Posts.Image -> {
                isSaved = if (postInfo.isSaved == 1) 0 else 1
                postInfo.id

            }
            is AllPostData.Body.Posts.Document -> {
                isSaved = if (postInfo.isSaved == 1) 0 else 1
                postInfo.id

            }
            is AllPostData.Body.Posts.Event -> {
                isSaved = if (postInfo.isSaved == 1) 0 else 1
                postInfo.id

            }
            is AllPostData.Body.Posts.Poll -> {
                isSaved = if (postInfo.isSaved == 1) 0 else 1
                postInfo.id

            }
            else -> {
                -1
            }
        }
        val body = PostReactionBody(
            artistId = artistId,
            postId = id
        )
        val aa = if (isSaved == 1) {
            postRepository.savePostCall(body)
        } else {
            postRepository.unsavePostCall(body)
        }
        return aa.flatMap {
                if (it.responseCode == 200) {
                    when (postInfo) {
                        is AllPostData.Body.Posts.Video -> {
                            postInfo.isSaved = isSaved
                            postInfo.saveCount =
                                (postInfo.saveCount ?: 0).plus(if (isSaved == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Music -> {
                            postInfo.isSaved = isSaved
                            postInfo.saveCount =
                                (postInfo.saveCount ?: 0).plus(if (isSaved == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Image -> {
                            postInfo.isSaved = isSaved
                            postInfo.saveCount =
                                (postInfo.saveCount ?: 0).plus(if (isSaved == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Document -> {
                            postInfo.isSaved = isSaved
                            postInfo.saveCount =
                                (postInfo.saveCount ?: 0).plus(if (isSaved == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Event -> {
                            postInfo.isSaved = isSaved
                            postInfo.saveCount =
                                (postInfo.saveCount ?: 0).plus(if (isSaved == 1) 1 else -1)
                        }
                        is AllPostData.Body.Posts.Poll -> {
                            postInfo.isSaved = isSaved
                            postInfo.saveCount =
                                (postInfo.saveCount ?: 0).plus(if (isSaved == 1) 1 else -1)
                        }
                    }
                    Single.just(postInfo)
                } else {
                    Single.error<Exception>(Exception(""))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun sharePost(postInfo: Any): Single<Any> {
        var isShared = 0
        val id = when (postInfo) {
            is AllPostData.Body.Posts.Video -> {
                isShared = postInfo.isShared ?: 0
                postInfo.id
            }
            is AllPostData.Body.Posts.Music -> {
                isShared = postInfo.isShared ?: 0
                postInfo.id

            }
            is AllPostData.Body.Posts.Image -> {
                isShared = postInfo.isShared ?: 0
                postInfo.id

            }
            is AllPostData.Body.Posts.Document -> {
                isShared = postInfo.isShared ?: 0
                postInfo.id
            }
            is AllPostData.Body.Posts.Event -> {
                isShared = postInfo.isShared ?: 0
                postInfo.id

            }
            is AllPostData.Body.Posts.Poll -> {
                isShared = postInfo.isShared ?: 0
                postInfo.id
            }
            else -> {
                -1
            }
        }
        if (isShared == 0) {
            val body = PostReactionBody(
                artistId = artistId,
                postId = id
            )
            return postRepository.sharePostCall(body)
                .flatMap {
                    if (it.responseCode == 200) {
                        when (postInfo) {
                            is AllPostData.Body.Posts.Video -> {
                                postInfo.isShared = isShared
                                postInfo.shareCount = (postInfo.shareCount ?: 0).plus(1)
                            }
                            is AllPostData.Body.Posts.Music -> {
                                postInfo.isShared = isShared
                                postInfo.shareCount = (postInfo.shareCount ?: 0).plus(1)
                            }
                            is AllPostData.Body.Posts.Image -> {
                                postInfo.isShared = isShared
                                postInfo.shareCount = (postInfo.shareCount ?: 0).plus(1)
                            }
                            is AllPostData.Body.Posts.Document -> {
                                postInfo.isShared = isShared
                                postInfo.shareCount = (postInfo.shareCount ?: 0).plus(1)
                            }
                            is AllPostData.Body.Posts.Event -> {
                                postInfo.isShared = isShared
                                postInfo.shareCount = (postInfo.shareCount ?: 0).plus(1)
                            }
                            is AllPostData.Body.Posts.Poll -> {
                                postInfo.isShared = isShared
                                postInfo.shareCount = (postInfo.shareCount ?: 0).plus(1)
                            }
                        }
                        Single.just(postInfo)
                    } else {
                        Single.error<Exception>(Exception(""))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
        } else {
            return Single.just(
                postInfo
            ).observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun votePoll(
        postInfo: AllPostData.Body.Posts.Poll,
        optionId: Int
    ): Single<AllPostData.Body.Posts.Poll> {
        val isVoted = if (postInfo.isVoted == 1) 0 else 1
        val id = postInfo.id

        val body = NewVoteBody(
            artistId = artistId,
            optionId = optionId,
            postId = id
        )
        return postRepository.votePostCall(body)
            .flatMap {
                if (it.responseCode == 200) {
                    postInfo.isVoted = isVoted
                    val item = postInfo.options?.find { p -> p?.id == optionId }
                    item?.count = (item?.count ?: 0).plus(if (isVoted == 1) 1 else -1)
                    return@flatMap Single.just(postInfo)
                } else {
                    return@flatMap Single.error(Exception(""))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun reportPost(postId: Int): Single<PostApiResponse> {
        val body = PostReportBody(
            data = PostReportBody.Data(
                artistId = artistId,
                postId = postId
            )
        )
        return postRepository.reportPostCall(body).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun setIsScreenOff(boolean: Boolean) {
        isScreenOff.value = boolean
    }

    fun fetchLocation(location: Location, context: Context): LiveData<String> {
        val dataObserver = MutableLiveData<String>()
        compositeDisposable.add(
            Observable.just(location).flatMap {
            return@flatMap Observable.create<String> { emitter ->
                val geocoder = Geocoder(context, Locale.ENGLISH)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(location.latitude,
                        location.longitude,
                        1,
                        Geocoder.GeocodeListener { addresses ->
                            getAddressResult(emitter, addresses)
                        })
                } else {
                    val addresses = geocoder.getFromLocation(
                        location.latitude, location.longitude, 1
                    )
                    if (addresses != null) {
                        getAddressResult(emitter, addresses)
                    }
                }
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.d(TAG, "getCurrentLocation: $it")
            dataObserver.value = it
        }, {
            Log.e(TAG, "getCurrentLocation: ", it)
        }))
        return dataObserver
    }


    private fun getAddressResult(
        emitter: ObservableEmitter<String>, addresses: MutableList<Address>?
    ) {
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses.get(0)
            emitter.onNext(
                ("${address.subLocality ?: address.locality ?: address.subAdminArea ?: ""}, ${address.adminArea ?: ""}").removePrefix(
                    ","
                ).removeSuffix(",").removePrefix(",")
            )
            emitter.onComplete()
        } else {
            emitter.onComplete()
        }
    }

}