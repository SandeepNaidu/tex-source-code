package com.project.tex.post

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.project.tex.GlobalApplication
import com.project.tex.addmedia.FileUtils
import com.project.tex.addmedia.upload.FileUploader
import com.project.tex.base.BaseViewModel
import com.project.tex.db.table.LocalPost
import com.project.tex.main.model.PostApiResponse
import com.project.tex.onboarding.Constants
import com.project.tex.utils.ViewUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.apache.commons.lang3.StringUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class CreatePostViewModel : BaseViewModel() {

    private val THUMBSIZE: Int = ViewUtils.dpToPx(100)
    private val TAG: String = CreatePostViewModel::class.java.simpleName

    private val createPostRepository: CreatePostRepository by lazy {
        CreatePostRepository
    }

    private val subject: PublishSubject<String> = PublishSubject.create()
    private val tagMLD = MutableLiveData<String>()
    private val tagIdLD: LiveData<String> = tagMLD
    private val isMuteLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    fun setTag(tag: String) {
        tagMLD.value = tag
    }

    public fun addPostToDb(url: String, bmp: Bitmap?, postId: String, postType: String) {
        compositeDisposable.add(Observable.just(if (bmp == null) File("") else storeImage(bmp))
            .flatMap { thumb ->
                Observable.just(
                    LocalPost(
                        postId = postId,
                        postLocalUri = url,
                        postThumb = thumb.path,
                        postType = postType
                    )
                )
            }.flatMap { take ->
                createPostRepository.db.localPostDao().insertAll(take).toObservable()
            }.subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribe({
                Log.d("Insert", "Post Added")
            }, {
                Log.e("Error Insert", "${it.message}", it)
            })
        )
    }

    public fun addPostToDb(url: String, thumbUrl: String?, postId: String, postType: String) {
        if (StringUtils.isEmpty(thumbUrl)) {
            addPostToDb(url, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), postId, postType)
        } else compositeDisposable.add(Observable.just(thumbUrl).subscribeOn(Schedulers.io())
            .flatMap {
                val ThumbImage = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(thumbUrl), THUMBSIZE, THUMBSIZE
                )
                Observable.just(ThumbImage)
            }.map {
                addPostToDb(url, it, postId, postType)
            }.subscribe({
                Log.d(TAG, "addPostToDb: done")
            }, {
                Log.e(TAG, "addPostToDb: ", it)
            })
        )

    }

    public fun getAllTakes(type: String): LiveData<List<LocalPost>> {
        return createPostRepository.db.localPostDao().loadPostByType(type)
    }

    private fun storeImage(image: Bitmap): File {
        val cw = ContextWrapper(GlobalApplication.instance)
        val directory: File = cw.getDir("imageDir", MODE_PRIVATE)
        val file = File(directory, "IMG_${System.currentTimeMillis()}" + ".jpg")
        if (!file.exists()) {
            Log.d("path", file.toString())
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    public fun setText(text: String) {
        _text.value = text
    }

    fun deleteFileFromDB(postId: String) {
        createPostRepository.db.localPostDao().deletePostById(postId)
    }

    fun createMediaPost(
        thumbImg: String,
        postContentUrl: String,
        postType: String,
        postCaption: String,
        hashTags: String,
        latLong: String,
        address: String
    ): LiveData<PostApiResponse> {
        val createPost = MutableLiveData<PostApiResponse>()
        createPostRepository.createPost(
            thumbImg, postType, postContentUrl, postCaption, hashTags, latLong, address, createPost
        )
        return createPost
    }

    fun createEventPost(
        description: String,
        eventType: String,
        eventFormat: String,
        event: String,
        eventTime: String,
        eventExternalLink: String,
        latLong: String,
        bannerUrl: Uri,
        address: String
    ): LiveData<PostApiResponse> {
        val createPost = MutableLiveData<PostApiResponse>()
        val url: Array<String> = arrayOf("", "")
        startUploadEventBanner(bannerUrl)?.let {
            compositeDisposable.add(
                it.subscribe(
                    {
                        url[0] = it
                    }, {
                        createPost.value = PostApiResponse(responseCode = 400, body = null)
                        Log.e(TAG, "onCropSuccess: ", it)
                    }, {
                        createPostRepository.createEvent(
                            description = description,
                            eventType = eventType,
                            eventFormat = eventFormat,
                            event = event,
                            eventTime = eventTime,
                            eventExternalLink = eventExternalLink,
                            latLong = latLong,
                            url = url[0],
                            address = address,
                            createPost
                        )
                    })
            )
        }
        return createPost
    }

    fun createPollPost(
        question: String,
        options: Array<String>,
        duration: Int,
        latLong: String,
        address: String
    ): LiveData<PostApiResponse> {
        val createPost = MutableLiveData<PostApiResponse>()
        createPostRepository.createPoll(
            question = question,
            options = options,
            latLong = latLong,
            duration = duration,
            address = address,
            mutableLiveData = createPost
        )
        return createPost
    }

    fun startUploadPostData(video_uri: Uri, coverImg: Bitmap): Observable<String>? {
        return Observable.create<String> { emit ->
            uploadVideo(video_uri, object : FileUploader.Callback<Uri> {
                override fun onResult(isSuccessful: Boolean, data: Task<Uri>, e: Exception?) {
                    if (isSuccessful) {
                        emit.onNext(data.result.toString())
                        uploadThumb(coverImg, object : FileUploader.Callback<Uri> {
                            override fun onResult(
                                isSuccessful: Boolean, data: Task<Uri>, e: Exception?
                            ) {
                                emit.onNext(data.result.toString())
                                emit.onComplete()
                            }
                        })
                    }
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    fun startUploadDocumentPost(docUri: Uri, coverImg: Bitmap): Observable<String>? {
        return Observable.create<String> { emit ->
            uploadDocument(docUri, object : FileUploader.Callback<Uri> {
                override fun onResult(isSuccessful: Boolean, data: Task<Uri>, e: Exception?) {
                    if (isSuccessful) {
                        emit.onNext(data.result.toString())
                        uploadThumb(coverImg, object : FileUploader.Callback<Uri> {
                            override fun onResult(
                                isSuccessful: Boolean, data: Task<Uri>, e: Exception?
                            ) {
                                emit.onNext(data.result.toString())
                                emit.onComplete()
                            }
                        })
                    }
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    fun startUploadMusicPost(musicUri: Uri, thumb: Uri?): Observable<String>? {
        return Observable.create<String> { emit ->
            val bmp = if (thumb != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            GlobalApplication.instance.contentResolver, thumb
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(
                        GlobalApplication.instance.contentResolver, thumb
                    )
                }
            } else {
                null
            }
            uploadMusic(musicUri, object : FileUploader.Callback<Uri> {
                override fun onResult(isSuccessful: Boolean, data: Task<Uri>, e: Exception?) {
                    if (isSuccessful) {
                        emit.onNext(data.result.toString())
                        if (bmp != null) {
                            uploadThumb(bmp, object : FileUploader.Callback<Uri> {
                                override fun onResult(
                                    isSuccessful: Boolean, data: Task<Uri>, e: Exception?
                                ) {
                                    emit.onNext(data.result.toString())
                                    emit.onComplete()
                                }
                            })
                        } else {
                            emit.onComplete()
                        }
                    }
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    fun startUploadEventBanner(eventBanner: Uri): Observable<String>? {
        return Observable.create<String> { emit ->
            uploadMusic(eventBanner, object : FileUploader.Callback<Uri> {
                override fun onResult(isSuccessful: Boolean, data: Task<Uri>, e: Exception?) {
                    if (isSuccessful) {
                        emit.onNext(data.result.toString())
                        emit.onComplete()
                    }
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    fun startUploadImagePostData(video_uri: Uri, coverImg: Bitmap): Observable<String>? {
        return Observable.create<String> { emit ->
            uploadImage(video_uri, object : FileUploader.Callback<Uri> {
                override fun onResult(isSuccessful: Boolean, data: Task<Uri>, e: Exception?) {
                    if (isSuccessful) {
                        emit.onNext(data.result.toString())
                        uploadThumb(coverImg, object : FileUploader.Callback<Uri> {
                            override fun onResult(
                                isSuccessful: Boolean, data: Task<Uri>, e: Exception?
                            ) {
                                emit.onNext(data.result.toString())
                                emit.onComplete()
                            }
                        })
                    }
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    private fun uploadDocument(docUri: Uri, callback: FileUploader.Callback<Uri>) {
        FileUploader().uploadDocument(
            Uri.fromFile(File(FileUtils.getPath(GlobalApplication.instance, docUri)!!)), callback
        )
    }

    private fun uploadMusic(docUri: Uri, callback: FileUploader.Callback<Uri>) {
        FileUploader().uploadAudio(
            Uri.fromFile(File(FileUtils.getPath(GlobalApplication.instance, docUri)!!)), callback
        )
    }

    private fun uploadBanner(docUri: Uri, callback: FileUploader.Callback<Uri>) {
        FileUploader().uploadBanner(
            Uri.fromFile(File(FileUtils.getPath(GlobalApplication.instance, docUri)!!)), callback
        )
    }

    private fun uploadVideo(video_uri: Uri, callback: FileUploader.Callback<Uri>) {
        FileUploader().uploadPostVideo(
            Uri.fromFile(File(FileUtils.getPath(GlobalApplication.instance, video_uri)!!)), callback
        )
    }

    private fun uploadImage(image_uri: Uri, callback: FileUploader.Callback<Uri>) {
        FileUploader().uploadPostImage(
            Uri.fromFile(File(FileUtils.getPath(GlobalApplication.instance, image_uri)!!)), callback
        )
    }

    private fun uploadThumb(thumb: Bitmap, callback: FileUploader.Callback<Uri>) {
        compositeDisposable.add(Observable.just(thumb).subscribeOn(Schedulers.io()).flatMap { bmp ->
            return@flatMap Observable.just(convertBitmapToFile(bmp))
        }.subscribe({ file ->
            FileUploader().uploadThumbImage(Uri.fromFile(file), callback)
        }, { e ->
            Log.e(TAG, "UploadPhoto failed", e)
        })
        )
    }

    private fun convertBitmapToFile(bitmap: Bitmap): File {
        //create a file to write bitmap data
        val f = File(GlobalApplication.instance.cacheDir, "cover_img_${System.nanoTime()}")
        f.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapdata: ByteArray = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return f
    }

    fun emit() {
        tagIdLD.value?.let { s ->
            subject.onNext(s)
        }
    }

    fun subscribeSubject() {
        compositeDisposable.add(subject.debounce(
            Constants.DEBOUNCE_INPUT_REQUEST_TIME, TimeUnit.MILLISECONDS
        ).switchMap { t ->
            Observable.just(t)
        }.observeOn(AndroidSchedulers.mainThread()).subscribe { name ->
//                getTagsByName(name)
        })
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun deleteF(uri: Uri) {
        try {
            FileUtils.getPath(GlobalApplication.instance, uri)?.let { File(it).delete() }
                ?: kotlin.run {
                    File(GlobalApplication.instance.cacheDir, uri.toString()).delete()
                }
        } catch (e: Exception) {
            Log.e(TAG, "deleteF: ", e)
        }
    }

    fun setAudioMute(isMute: Boolean) {
        this.isMuteLiveData.value = isMute
    }

    fun getIsMute(): MutableLiveData<Boolean> {
        return isMuteLiveData
    }

    fun fetchLocation(location: Location, context: Context): LiveData<String> {
        val dataObserver = MutableLiveData<String>()
        compositeDisposable.add(Observable.just(location).flatMap {
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
        })
        )
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