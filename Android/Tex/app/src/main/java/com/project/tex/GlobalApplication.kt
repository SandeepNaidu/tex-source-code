package com.project.tex

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.danikula.videocache.HttpProxyCacheServer


//import io.stipop.Stipop

open class GlobalApplication : Application() {
    companion object {
        private val TAG = GlobalApplication::class.java.simpleName
        lateinit var instance: GlobalApplication
            private set
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private var proxy: HttpProxyCacheServer? = null

    open fun getProxy(context: Context): HttpProxyCacheServer? {
        return proxy ?: newProxy().also { proxy = it }
    }

    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer.Builder(this)
            .maxCacheSize(1024 * 1024 * 1024)// 1gb cache size
            .build()
    }
}