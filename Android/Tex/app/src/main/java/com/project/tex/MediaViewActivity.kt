package com.project.tex

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
//import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso

class MediaViewActivity : AppCompatActivity() {
    var sharedPref: NightMode? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = NightMode(this)
        if (sharedPref!!.loadNightModeState() == "night") {
            setTheme(R.style.DarkTheme)
        } else if (sharedPref!!.loadNightModeState() == "dim") {
            setTheme(R.style.DimTheme)
        } else setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_media_view)

        //GetUri
        val uri = intent.getStringExtra("uri")
        val type = intent.getStringExtra("type")

        //Video
        if (type == "video") {
            val videoView = findViewById<VideoView>(R.id.videoView)
            videoView.visibility = View.VISIBLE
            videoView.setVideoURI(Uri.parse(uri))
            videoView.start()
            videoView.setOnPreparedListener { mp: MediaPlayer -> mp.isLooping = true }
            val mediaController = MediaController(this@MediaViewActivity)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
        } else if (type == "image") {
//            val photoView = findViewById<View>(R.id.photo_view) as PhotoView
//            photoView.visibility = View.VISIBLE
//            Picasso.get().load(uri).into(photoView)
        }
    }
}