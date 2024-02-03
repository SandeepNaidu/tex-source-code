package com.project.tex.main.ui.avid.avidCamera.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityEditCoverBinding
import com.project.tex.main.ui.avid.avidCamera.utils.ExtractVideoInfoUtil
import com.project.tex.main.ui.avid.avidCamera.utils.UIUtils
import com.project.tex.main.ui.search.AvidViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class EditCoverActivity : BaseActivity<ActivityEditCoverBinding, AvidViewModel>(),
    View.OnClickListener {
    private lateinit var videoInfo: ExtractVideoInfoUtil
    private val TAG: String = EditCoverActivity::class.java.simpleName

    override fun getViewBinding() = ActivityEditCoverBinding.inflate(layoutInflater)

    override fun getViewModelInstance() =
        ViewModelProvider(this)[AvidViewModel::class.java]

    private lateinit var coverImg: Bitmap
    private lateinit var avidVideo: Uri
    private var selectedTime: Long = 0L
    private var list = arrayListOf<Bitmap>()
    private var frameTimeList = arrayListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            avidVideo = intent.getParcelableExtra("videoPath", Uri::class.java)!!
        } else {
            avidVideo = intent.getParcelableExtra("videoPath")!!
        }

        videoInfo = ExtractVideoInfoUtil(Uri.parse(avidVideo.toString()))
        coverImg = videoInfo.extractFrame()
        Glide.with(this).load(coverImg)
            .transform(CenterCrop(), RoundedCorners(UIUtils.dp2Px(4)))
            .into(_binding.avidPreviewImg)
        _binding.backBtn.setOnClickListener(this)
        _binding.tvConfirm.setOnClickListener(this)
        _binding.imgPrev1.setOnClickListener(this)
        _binding.imgPrev2.setOnClickListener(this)
        _binding.imgPrev3.setOnClickListener(this)
        _binding.imgPrev4.setOnClickListener(this)
        _binding.imgPrev5.setOnClickListener(this)
        val viewList = arrayOf(
            _binding.imgPrev1,
            _binding.imgPrev2,
            _binding.imgPrev3,
            _binding.imgPrev4,
            _binding.imgPrev5
        )

        viewModel.add(Observable.just(videoInfo.videoLength).subscribeOn(Schedulers.io())
            .flatMap {
                val max: Long = it.toLongOrNull() ?: 0L
                Observable.just(max)
            }.map {
                val dividedFrame = it / 5 - 100
                for (frameCount in 1..5) {
                    val frameTime = dividedFrame * frameCount
                    list.add(videoInfo.extractFrame(frameTime))
                    frameTimeList.add(frameTime)
                }
                list
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.forEachIndexed { index, bitmap ->
                    Glide.with(this).load(bitmap)
                        .transform(CenterCrop(), RoundedCorners(UIUtils.dp2Px(4)))
                        .into(viewList[index])
                }
            }, {
                msg.showShortMsg("Failed to extract images from video!")
            })
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_btn -> {
                finish()
            }
            R.id.tv_confirm -> {
                setResult(Activity.RESULT_OK, Intent().putExtra("frameTime", selectedTime))
                finish()
            }
            R.id.img_prev_1 -> {
                selectedTime = frameTimeList[0]
                loadNewCoverImage()
                resetAllSelection()
                _binding.frameParent1.strokeWidth = UIUtils.dp2Px(2)
                _binding.frameParent1.cardElevation = UIUtils.dp2Px(8).toFloat()

            }
            R.id.img_prev_2 -> {
                selectedTime = frameTimeList[1]
                loadNewCoverImage()
                resetAllSelection()
                _binding.frameParent2.strokeWidth = UIUtils.dp2Px(2)
                _binding.frameParent2.cardElevation = UIUtils.dp2Px(8).toFloat()

            }
            R.id.img_prev_3 -> {
                selectedTime = frameTimeList[2]
                loadNewCoverImage()
                resetAllSelection()
                _binding.frameParent3.strokeWidth = UIUtils.dp2Px(2)
                _binding.frameParent3.cardElevation = UIUtils.dp2Px(8).toFloat()

            }
            R.id.img_prev_4 -> {
                selectedTime = frameTimeList[3]
                loadNewCoverImage()
                resetAllSelection()
                _binding.frameParent4.strokeWidth = UIUtils.dp2Px(2)
                _binding.frameParent4.cardElevation = UIUtils.dp2Px(8).toFloat()

            }
            R.id.img_prev_5 -> {
                selectedTime = frameTimeList[4]
                loadNewCoverImage()
                resetAllSelection()
                _binding.frameParent5.strokeWidth = UIUtils.dp2Px(2)
                _binding.frameParent5.cardElevation = UIUtils.dp2Px(8).toFloat()

            }
        }
    }

    private fun resetAllSelection() {
        _binding.frameParent1.strokeWidth = UIUtils.dp2Px(0)
        _binding.frameParent1.cardElevation = UIUtils.dp2Px(0).toFloat()
        _binding.frameParent2.strokeWidth = UIUtils.dp2Px(0)
        _binding.frameParent2.cardElevation = UIUtils.dp2Px(0).toFloat()
        _binding.frameParent3.strokeWidth = UIUtils.dp2Px(0)
        _binding.frameParent3.cardElevation = UIUtils.dp2Px(0).toFloat()
        _binding.frameParent4.strokeWidth = UIUtils.dp2Px(0)
        _binding.frameParent4.cardElevation = UIUtils.dp2Px(0).toFloat()
        _binding.frameParent5.strokeWidth = UIUtils.dp2Px(0)
        _binding.frameParent5.cardElevation = UIUtils.dp2Px(0).toFloat()
    }

    private fun loadNewCoverImage() {
        coverImg = videoInfo.extractFrame(selectedTime)
        Glide.with(this).load(coverImg)
            .transform(CenterCrop(), RoundedCorners(UIUtils.dp2Px(4)))
            .into(_binding.avidPreviewImg)
    }

}