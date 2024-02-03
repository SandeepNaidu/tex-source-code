package com.project.tex.aritist_profile.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.project.tex.GlobalApplication
import com.project.tex.addmedia.FileUtils
import com.project.tex.addmedia.upload.FileUploader
import com.project.tex.aritist_profile.ProfileRepository
import com.project.tex.aritist_profile.model.*
import com.project.tex.aritist_profile.ui.ProfileActivity
import com.project.tex.base.BaseViewModel
import com.project.tex.base.data.SharedPrefsKey
import com.project.tex.main.model.AvidByArtistListResponse
import com.project.tex.main.model.PostApiResponse
import com.project.tex.settings.viewmodel.SettingItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.time.DateFormatUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ProfileViewModel() : BaseViewModel() {
    private val TAG: String = "PROFILEVIEWMODEL"
    private val profileRepository: ProfileRepository by lazy {
        ProfileRepository
    }

    private val isScreenOff = MutableLiveData<Boolean>(false)
    public val isScreenOffObserver: LiveData<Boolean> = isScreenOff

    private final val userId = MutableLiveData<Int>(-1)
    private final val isSelfView = MutableLiveData(true)

    fun setIsScreenOff(boolean: Boolean) {
        isScreenOff.value = boolean
    }

    fun setUserId(id: Int) {
        userId.value = id
        isSelfView.value = id == artistId
    }

    fun getIsSelfView(): Boolean {
        return isSelfView.value == true
    }

    private val portfolio = MutableLiveData<PortfolioListResponse>()
    public val portfolioLd = portfolio

    private val profileData = MutableLiveData<UserProfileData.Body.Users.Body?>()
    public val profileDataLd = profileData

    private val experience = MutableLiveData<List<ExperienceListResponse.Body.Experience?>>()
    public val experienceLd = experience

    private val userName = MutableLiveData<String>()
    public val userNameData: LiveData<String> = userName

    private val artistId: Int by lazy {
        profileRepository.dataKeyValue.getValueInt(SharedPrefsKey.USER_ID)
    }

    fun getUserId(): Int {
        return artistId
    }

    public fun setPortfolioData(data: PortfolioListResponse) {
        portfolio.value = data
    }

    public fun setProfileData(data: UserProfileData.Body.Users.Body?) {
        profileData.value = data
    }

    public fun setExperienceData(data: List<ExperienceListResponse.Body.Experience?>) {
        experience.value = data
    }

    public fun getProfileSettingPageItem(): List<SettingItem> {
        return listOf(
            SettingItem(0, "Status settings", 0),
            SettingItem(1, "Contact Details", 0),
        )
    }

    public fun setUserName() {
        userName.value =
            (profileRepository.dataKeyValue.getValueString(SharedPrefsKey.USER_FIRST_NAME)
                ?: "") + " " + (profileRepository.dataKeyValue.getValueString(
                SharedPrefsKey.USER_LASTNAME
            ) ?: "")
    }

    public fun getGender(): String? {
        return profileRepository.dataKeyValue.getValueString(SharedPrefsKey.GENDER)
    }

    public fun getAllPostListing(): Single<MutableList<Any>> {
        return profileRepository.getAllMediaPostList(userId.value ?: -1).doOnSubscribe {
            compositeDisposable.add(it)
        }
    }

    public fun getAllPortfolio(): Single<PortfolioListResponse> {
        return profileRepository.getAllPortfolios(userId.value ?: -1)
    }

    public fun getAvids(): Single<AvidByArtistListResponse> {
        return profileRepository.getAllAvidListing(userId.value ?: -1)
    }

    public fun getProfileData(): Single<UserProfileData> {
        return profileRepository.getProfileData(userId.value ?: -1)
    }

    public fun getUpdateProfileData(activity: ProfileActivity) {
        compositeDisposable.add(
            getProfileData()
                .subscribe({
                    if (it.responseCode == 200 && it.body?.users != null) {
                        it.body.users.body?.let { userD ->
                            if (userD.isNotEmpty()) {
                                setProfileData(userD[0])
                            }
                        }
                    } else {
                        activity.msg.showShortMsg("Failed to load profile data")
                    }
                }, {
                    activity._binding.content.profileCoverPager.adapter?.notifyDataSetChanged()
                    activity.msg.showShortMsg("Failed to load profile data")
                })
        )
    }

    fun startUploadFilePost(docUri: Uri, coverImg: Bitmap): Observable<String> {
        return Observable.create<String> { emit ->
            uploadFile(docUri, object : FileUploader.Callback<Uri> {
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

    fun startUploadProfileImg(docUri: Uri): Observable<String> {
        return Observable.create<String> { emit ->
            uploadProfileFile(docUri, object : FileUploader.Callback<Uri> {
                override fun onResult(isSuccessful: Boolean, data: Task<Uri>, e: Exception?) {
                    if (isSuccessful) {
                        emit.onNext(data.result.toString())
                        emit.onComplete()
                    }
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    private fun uploadProfileFile(fileUri: Uri, callback: FileUploader.Callback<Uri>) {
        FileUploader().uploadProfileImage(
            Uri.fromFile(File(FileUtils.getPath(GlobalApplication.instance, fileUri)!!)), callback
        )
    }

    public fun createPortfolio(
        fileUrl: String,
        thumbUrl: String,
        type: String
    ): Single<PostApiResponse> {
        return profileRepository.createPortfolio(artistId, fileUrl, thumbUrl, type)
    }

    public fun createExperience(
        companyName: String,
        jobTitle: String,
        startDate: String,
        endDate: String,
        description: String
    ): Single<PostApiResponse> {
        return profileRepository.createExperience(
            artistId,
            companyName,
            description,
            jobTitle,
            startDate,
            endDate
        )
    }

    public fun updateExperience(
        eId: Int,
        companyName: String,
        jobTitle: String,
        startDate: String,
        endDate: String,
        description: String
    ): Single<PostApiResponse> {
        return profileRepository.updateExperience(
            eId,
            companyName,
            description,
            jobTitle,
            startDate,
            endDate
        )
    }

    public fun markAsFresher(
        isFresher: Int
    ): Single<GenericUpdateResponse> {
        return profileRepository.markUserAsFresher(
            artistId,
            isFresher
        )
    }


    public fun updateVisibility(
        isVisible: Int
    ): Single<GenericUpdateResponse> {
        return profileRepository.updateVisibility(
            artistId,
            isVisible
        )
    }

    public fun updateProfileImageLastUpdate(
    ) {
        val lastUpdatedOn = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT
            .format(Date())
        add(
            profileRepository.updateProfileImageLastUpdatedOn(
                artistId,
                lastUpdatedOn
            ).subscribe({

            }, {
                Log.e(TAG, "updateProfileImageLastUpdate: ", it)
            })
        )
    }

    public fun updateContactLastUpdate(
    ) {
        val lastUpdatedOn = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT
            .format(Date())
        add(
            profileRepository.updateContactLastUpdatedOn(
                artistId,
                lastUpdatedOn
            ).subscribe({

            }, {
                Log.e(TAG, "updateProfileImageLastUpdate: ", it)
            })
        )
    }

    public fun updateStatusSetting(
        statusAvailability: String,
        onlineStatus: String
    ): Single<GenericUpdateResponse> {
        return profileRepository.updateStatusSetting(
            artistId,
            statusAvailability,
            onlineStatus
        )
    }

    public fun getAllExperience(): Single<ExperienceListResponse> {
        return profileRepository.getAllExperience(userId.value ?: -1)
    }

    public fun getProfileAnalytics(): Single<ProfileAnalytics> {
        return profileRepository.getProfileAnalytics(artistId)
    }

    public fun getProfileActions(): Single<ProfileAnalyticsData> {
        return profileRepository.getProfileActionData(userId.value ?: -1, artistId)
    }

    private fun uploadThumb(thumb: Bitmap, callback: FileUploader.Callback<Uri>) {
        compositeDisposable.add(Observable.just(thumb).subscribeOn(Schedulers.io()).flatMap { bmp ->
            return@flatMap Observable.just(convertBitmapToFile(bmp))
        }.subscribe({ file ->
            FileUploader().uploadThumbImage(Uri.fromFile(file), callback)
        }, { e ->
            Log.e("asd", "UploadPhoto failed", e)
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

    private fun uploadFile(docUri: Uri, callback: FileUploader.Callback<Uri>) {
        FileUploader().uploadPortfolioFile(
            Uri.fromFile(File(FileUtils.getPath(GlobalApplication.instance, docUri)!!)), callback
        )
    }

    fun getHeight(): CharSequence? {
        return null
    }

    fun getAge(): CharSequence? {
        return null
    }

    fun getMobile(): CharSequence? {
        return profileRepository.dataKeyValue.getValueString(SharedPrefsKey.USER_CONTACT)
    }

    fun getEmail(): CharSequence? {
        return profileRepository.dataKeyValue.getValueString(SharedPrefsKey.USER_EMAIL)
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        bio: String,
        gender: String,
        dob: String,
        age: String,
        height: String,
        city: String,
        profileImg: String
    ): Single<GenericUpdateResponse> {
        return profileRepository.updateProfileData(
            artistId,
            firstName = firstName,
            lastName,
            bio,
            gender,
            dob,
            age,
            height,
            city,
            profileImg
        )
    }

    fun saveUserData(user: UserProfileData.Body.Users.Body) {
        profileRepository.dataKeyValue.save(SharedPrefsKey.USER_FIRST_NAME, user.firstName ?: "")
        profileRepository.dataKeyValue.save(SharedPrefsKey.USER_LASTNAME, user.lastName ?: "")
        profileRepository.dataKeyValue.save(SharedPrefsKey.USER_EMAIL, user.email ?: "")
        profileRepository.dataKeyValue.save(SharedPrefsKey.GENDER, user.gender ?: "")
        profileRepository.dataKeyValue.save(SharedPrefsKey.USER_BIO, user.bio ?: "")
        profileRepository.dataKeyValue.save(SharedPrefsKey.USER_CONTACT, user.contactNumber ?: "")
        profileRepository.dataKeyValue.save(SharedPrefsKey.USER_PROFILE_IMG, user.profileImage)
    }

    fun updateContact(toString: String): Single<GenericUpdateResponse> {
        return profileRepository.updateMob(artistId, toString)
    }

    fun updateEmail(toString: String): Single<GenericUpdateResponse> {
        return profileRepository.updateEmail(artistId, toString)
    }

    fun updateAltMob(toString: String): Single<GenericUpdateResponse> {
        return profileRepository.updateAltMob(artistId, toString)
    }

    fun updateAltEmail(toString: String): Single<GenericUpdateResponse> {
        return profileRepository.updateAltEmail(artistId, toString)
    }

    fun saveProfile(toSave: Int): Single<GenericUpdateResponse> {
        return profileRepository.saveProfile(userId.value ?: -1, artistId, toSave)
    }

    fun shareProfile(): Single<GenericUpdateResponse> {
        return profileRepository.shareProfile(userId.value ?: -1, artistId)
    }

    fun likeProfile(islike: Int): Single<GenericUpdateResponse> {
        return profileRepository.likeProfile(userId.value ?: -1, artistId, islike)
    }

    fun viewProfile() {
        add(
            profileRepository.viewProfile((userId.value ?: -1).toString(), artistId.toString())
                .subscribe({
                    Log.d(TAG, "viewProfile: yes")
                }, {
                    Log.e(TAG, "viewProfile: ", it)
                })
        )
    }

}