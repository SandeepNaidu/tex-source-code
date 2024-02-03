package com.project.tex.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {
    fun add(subscribe: Disposable) {
        compositeDisposable.add(subscribe)
    }

    val compositeDisposable = CompositeDisposable()
        get() = field
}