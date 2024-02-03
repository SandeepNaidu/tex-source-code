package com.project.tex.recruiter.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.tex.main.model.AvidListResponse
import com.project.tex.post.CreatePostRepository
import io.reactivex.Single

class RecHomeViewModel : ViewModel() {
    private val mutableAvidLiveData = MutableLiveData<AvidListResponse>()
    private val postRepository: CreatePostRepository by lazy {
        CreatePostRepository
    }
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    public fun getAvids(): Single<AvidListResponse> {
        return postRepository.getAllAvidListing()
    }
}