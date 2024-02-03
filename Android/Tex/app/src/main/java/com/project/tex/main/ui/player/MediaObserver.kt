package com.project.tex.main.ui.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import java.util.concurrent.atomic.AtomicBoolean

class MediaObserver(val mediaPlayer: MediaPlayer, val progress: ProgressBar) : Runnable {
    init {

    }

    private val handler = Handler(Looper.getMainLooper())
    private val stop = AtomicBoolean(false)
    fun stop() {
        stop.set(true)
    }

    override fun run() {
        while (!stop.get()) {
            handler.post {
                try {
                    progress.progress = ((mediaPlayer.currentPosition))
                    Log.d("progress", "run: "+progress.progress)
                }catch (e: Exception) {
                    mediaPlayer.reset()
                    progress.progress = mediaPlayer.getCurrentPosition()
                }
            }
            Thread.sleep(100)
        }
    }
}