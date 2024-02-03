package com.project.tex.addmedia.upload

import android.net.Uri
import android.util.Log
import com.cjt2325.cameralibrary.util.FileUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.project.tex.GlobalApplication
import com.project.tex.firebase.FirebaseStorageManager
import java.io.File

class FileUploader {

    fun uploadImage(imgFile: File, callback: Callback<Uri>) {
        //Upload
        uploadImage(FileUtils.getUri(imgFile), callback)
    }

    fun uploadImage(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instanceCoverImage
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    fun uploadProfileImage(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instanceProfileImage
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    fun uploadPostImage(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instancePhoto
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    fun uploadThumbImage(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instancePostThumb
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
                try {
                    FileUtils.getFile(GlobalApplication.instance, image_uri).delete()
                } catch (e: Exception) {
                    Log.e("uploadThumbImage", "uploadThumbImage: file delete on upload failed -", e)
                }
            }
        }
    }

    fun uploadAudio(audioFile: File, callback: Callback<Uri>) {
        //Upload
        uploadAudio(FileUtils.getUri(audioFile), callback)
    }

    fun uploadAudio(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instanceAudio
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    fun uploadBanner(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instanceEvent
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    fun uploadVideo(videoFile: File, callback: Callback<Uri>) {
        //Upload
        uploadVideo(FileUtils.getUri(videoFile), callback)
    }

    fun uploadVideo(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instanceAvidVideo
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    fun uploadPostVideo(postVideoUri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instanceVideo
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(postVideoUri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    fun uploadDocument(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instanceDocument
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    fun uploadPortfolioFile(image_uri: Uri, callback: Callback<Uri>) {
        //Upload
        val storageReference: StorageReference = FirebaseStorageManager.instancePortfolio
            .child(System.currentTimeMillis().toString())
        storageReference.putFile(image_uri).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnCompleteListener {
                callback.onResult(uriTask.isSuccessful, uriTask, uriTask.exception)
            }
        }
    }

    interface Callback<B> {
        fun onResult(isSuccessful: Boolean, data: Task<B>, e: Exception?)
    }
}