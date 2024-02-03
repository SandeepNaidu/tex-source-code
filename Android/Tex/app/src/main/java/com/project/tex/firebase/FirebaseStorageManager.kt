package com.project.tex.firebase

import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageManager {

    private val TAG: String = "FirebaseStorageManager"

    companion object {
        val instance = FirebaseStorage.getInstance()
        val instancePhoto = FirebaseStorage.getInstance().getReference("post_photo/")
        val instanceCoverImage = FirebaseStorage.getInstance().getReference("avid_cover/")
        val instanceAvidVideo = FirebaseStorage.getInstance().getReference("avid_video/")
        val instanceVideo = FirebaseStorage.getInstance().getReference("post_video/")
        val instanceAudio = FirebaseStorage.getInstance().getReference("post_audio/")
        val instanceEvent = FirebaseStorage.getInstance().getReference("post_event/")
        val instanceDocument = FirebaseStorage.getInstance().getReference("post_document/")
        val instancePortfolio = FirebaseStorage.getInstance().getReference("portfolio/")
        val instancePostThumb = FirebaseStorage.getInstance().getReference("post_thumb/")
        val instanceProfileImage = FirebaseStorage.getInstance().getReference("user_profile/")
    }

    private constructor() {

    }
}