package com.project.tex.main.ui.search

import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.project.tex.GlobalApplication
import com.project.tex.addmedia.FileUtils
import com.project.tex.addmedia.upload.FileUploader
import com.project.tex.base.BaseViewModel
import com.project.tex.db.table.Avid
import com.project.tex.db.table.AvidTakes
import com.project.tex.main.HomeRepository
import com.project.tex.main.model.*
import com.project.tex.onboarding.Constants
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit


class AvidViewModel : BaseViewModel() {

    private val TAG: String = AvidViewModel::class.java.simpleName

    private val homeRepository: HomeRepository by lazy {
        HomeRepository
    }

    private val subject: PublishSubject<String> = PublishSubject.create()
    private val tagMLD = MutableLiveData<String>()
    private val tagIdLD: LiveData<String> = tagMLD
    private val isMuteLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    private val allAvids: MutableLiveData<AvidListResponse> = MutableLiveData<AvidListResponse>()
    private val createAvid: MutableLiveData<LikeShareSaveResponse> =
        MutableLiveData<LikeShareSaveResponse>()
    private val tagByNameData: MutableLiveData<TagsListResponse> =
        MutableLiveData<TagsListResponse>()
    private val allTagData: MutableLiveData<TagsListResponse> = MutableLiveData<TagsListResponse>()

    private val allAvidsLD: LiveData<AvidListResponse> = allAvids
    private val createAvidLD: LiveData<LikeShareSaveResponse> = createAvid
    val tagByNameDataLD: LiveData<TagsListResponse> = tagByNameData
    private val allTagDataLD: LiveData<TagsListResponse> = allTagData

    private val artistId: Int by lazy {
        homeRepository.dataKeyValue.getValueInt("userId")
    }

    fun setTag(tag: String) {
        tagMLD.value = tag
    }

    public fun addTakeToDb(url: String, bmp: Bitmap, avidId: String) {
        compositeDisposable.add(Observable.just(bmp).flatMap { bm ->
            Observable.just(storeImage(bm))
        }.flatMap { thumb ->
            Observable.just(
                AvidTakes(
                    avidId = avidId,
                    videoUrl = url,
                    videoThumb = thumb.absolutePath
                )
            )
        }.flatMap { take ->
            homeRepository.db.avidTakesDao().insertAll(take).toObservable()
        }
            .subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribe(
                {
                    Log.d("Insert", "Avid Take Added")
                },
                {
                    Log.e("Error Insert", "${it.message}", it)
                }
            ))
    }

    public fun getAllTakes(): LiveData<List<AvidTakes>> {
        return homeRepository.db.avidTakesDao().getAll()
    }

    public fun deleteAllOld() {
        val dao = homeRepository.db.avidTakesDao();
        compositeDisposable.add(
            dao.getAllOlderAvidTakes()
                .subscribeOn(Schedulers.io())
                .flatMap { avidIds ->
                    Observable.create<AvidTakes> { emitter ->
                        avidIds.forEach {
                            emitter.onNext(it)
                        }
                        emitter.onComplete()
                    }.flatMap {
                        //delete file from storage
                        deleteF(Uri.parse(it.videoUrl))
                        Observable.just(it.avidTakeId)
                    }.flatMap {
                        //delete avid from database
                        deleteAvid(it).toObservable()
                    }.toList().toObservable()
                }.subscribe({
                    Log.d("DELETE_AVID", "deleteAllOld: completed")
                }, {
                    Log.e("DELETE_AVID", "Failed !!", it)
                }, {
                    Log.d(TAG, "deleteAllOld: Completed")
                })
        )
    }

    private fun storeImage(image: Bitmap): File {
        val cw =
            ContextWrapper(GlobalApplication.instance)
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

    fun deleteFileFromDB() {
//        homeRepository.db.avidTakesDao().delete(A)
    }

    fun getAllAvidList(): LiveData<AvidListResponse> {
        homeRepository.getAllAvidListing(allAvids)
        return allAvidsLD
    }

    fun createPost(
        coverImg: String,
        avidContentUrl: String,
        avidMode: String,
        avidTitle: String,
        avidCaption: String?,
        hashTag: String
    ): LiveData<LikeShareSaveResponse> {
        homeRepository.createPost(
            CreateAvidBody(
                caption = avidCaption,
                title = avidTitle,
                content = avidContentUrl,
                coverContent = coverImg,
                hashTag = hashTag,
                mode = avidMode
            ), createAvid
        )
        return createAvidLD
    }

    fun getAvidDetail(avidId: Int): LiveData<AvidDetailsResponse> {
        val avidDetails = MutableLiveData<AvidDetailsResponse>()
        homeRepository.getAvidDetail(avidId, avidDetails)
        return avidDetails
    }

    fun shareAvid(
        avidId: Int,
        isShare: Int,
        shareRes: MutableLiveData<LikeShareSaveResponse>
    ): LiveData<LikeShareSaveResponse> {
        homeRepository.shareAvidCall(
            ShareAvidBody(data = DataShare(artistId.toString(), avidId.toString(), isShare)),
            shareRes
        )
        return shareRes
    }

    fun saveAvid(
        avidId: Int,
        isSave: Int,
        saveRes: MutableLiveData<LikeShareSaveResponse>
    ): LiveData<LikeShareSaveResponse> {
        homeRepository.saveAvidCall(
            SaveAvidBody(data = Data(artistId.toString(), avidId.toString(), isSave)),
            saveRes
        )
        return saveRes
    }

    fun likeAvid(
        avidId: Int,
        isLike: Int,
        likeRes: MutableLiveData<LikeShareSaveResponse>
    ): LiveData<LikeShareSaveResponse> {
        homeRepository.likeAvidCall(
            LikeAvidBody(data = DataLike(artistId.toString(), avidId.toString(), isLike)),
            likeRes
        )
        return likeRes
    }

    fun getAllTags(): LiveData<TagsListResponse> {
        homeRepository.getAllTags(allTagData)
        return allTagDataLD
    }

    fun getTagsByName(name: String): LiveData<TagsListResponse> {
        homeRepository.getTagsByName(name, tagByNameData)
        return tagByNameDataLD
    }

    fun startUploadAvidData(video_uri: Uri, coverImg: Bitmap): Observable<String>? {
        return Observable.create<String> { emit ->
            uploadVideo(video_uri, object : FileUploader.Callback<Uri> {
                override fun onResult(isSuccessful: Boolean, data: Task<Uri>, e: Exception?) {
                    if (isSuccessful) {
                        emit.onNext(data.result.toString())
                        uploadPhoto(coverImg, object : FileUploader.Callback<Uri> {
                            override fun onResult(
                                isSuccessful: Boolean,
                                data: Task<Uri>,
                                e: Exception?
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

    private fun uploadVideo(video_uri: Uri, callback: FileUploader.Callback<Uri>) {
        FileUploader().uploadVideo(
            File(FileUtils.getPath(GlobalApplication.instance, video_uri)!!),
            callback
        )
    }

    private fun uploadPhoto(coverImg: Bitmap, callback: FileUploader.Callback<Uri>) {
        compositeDisposable.add(
            Observable.just(coverImg)
                .subscribeOn(Schedulers.io())
                .flatMap { bmp ->
                    return@flatMap Observable.just(convertBitmapToFile(bmp))
                }.subscribe({ file ->
                    FileUploader().uploadImage(file, callback)
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
            Constants.DEBOUNCE_INPUT_REQUEST_TIME,
            TimeUnit.MILLISECONDS
        ).switchMap { t ->
            Observable.just(t)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribe { name ->
                getTagsByName(name)
            })
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun saveDraft(
        avidId: String,
        avidLocalUri: String,
        title: String,
        caption: String,
        tags: String,
        avidMode: String,
        coverFrame: Int
    ): Observable<List<Long>> {
        return Observable.just(avidId).subscribeOn(Schedulers.io()).flatMap {
            Observable.just(
                Avid(
                    avidId = avidId,
                    avidLocalUri = avidLocalUri,
                    title = title,
                    caption = caption,
                    tags = tags,
                    avidMode = avidMode,
                    avidCoverFrame = coverFrame
                )
            )
        }.flatMap {
            homeRepository.db.avidDao().insertAll(it).toObservable()
        }.observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteAvid(avidId: Int): Single<Int> {
        return homeRepository.db.avidTakesDao().deleteAvidTakeById(avidId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteAvidByAvidId(avidId: String): Single<Int> {
        return homeRepository.db.avidTakesDao().deleteAvidTakeByAvidId(avidId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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

    fun getIsMute() : MutableLiveData<Boolean> {
        return isMuteLiveData
    }
}