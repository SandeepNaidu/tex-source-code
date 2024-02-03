package com.project.tex.main.ui.avid

import com.project.tex.model.ModelReel

object SampleAvid {
    private val list = mutableListOf<ModelReel>()
    val avidData: List<ModelReel>
        get() {
            if (!list.isEmpty()) return list
            list.add(ModelReel(video = "gs://friend-social-66b71.appspot.com/avid_video/pexels-rodnae-productions-8113104.mp4"))
            list.add(ModelReel(video = "gs://friend-social-66b71.appspot.com/avid_video/production ID_4835061.mp4"))
            list.add(ModelReel(video = "gs://friend-social-66b71.appspot.com/avid_video/vid_1234.mp4"))
            list.add(ModelReel(video = "gs://friend-social-66b71.appspot.com/avid_video/vid_1235.mp4"))
            list.add(ModelReel(video = "gs://friend-social-66b71.appspot.com/avid_video/vid_1236.mp4"))
            return list
        }
}