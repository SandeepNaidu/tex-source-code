package com.project.tex.main.ui.avid.avidCamera.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.animation.ScaleAnimation
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.arthenica.ffmpegkit.ReturnCode
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.ErrorListener
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.cjt2325.cameralibrary.listener.RecordStateListener
import com.google.android.gms.common.util.CollectionUtils
import com.parassidhu.tickingtimer.Shape
import com.project.tex.R
import com.project.tex.addmedia.FileUtils
import com.project.tex.base.activity.BaseActivity
import com.project.tex.base.view.CameraVideoButton
import com.project.tex.databinding.ActivityAvidCaptureBinding
import com.project.tex.main.ui.avid.avidCamera.adapter.SelfPromptTextAdapter
import com.project.tex.main.ui.avid.avidCamera.utils.AudioTrackToAacConvertor
import com.project.tex.main.ui.avid.avidCamera.utils.ExtractVideoInfoUtil
import com.project.tex.main.ui.avid.avidCamera.utils.VideoFFMpegUtil
import com.project.tex.main.ui.avid.avidCamera.utils.VideoUtil
import com.project.tex.main.ui.avid.avidCamera.view.NormalProgressDialog
import com.project.tex.main.ui.avid.dialog.SelfPromptDialog
import com.project.tex.main.ui.search.AvidViewModel
import com.project.tex.utils.MSG_TYPE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

class AvidCaptureActivity : BaseActivity<ActivityAvidCaptureBinding, AvidViewModel>() {

    override fun getViewBinding() = ActivityAvidCaptureBinding.inflate(layoutInflater)

    override fun getViewModelInstance() = ViewModelProvider(this).get(AvidViewModel::class.java)

    private var lastTakeRecordTime: Long = 0
    private val TAG: String = AvidCaptureActivity::class.java.simpleName
    private var isTakeDone: Boolean = false
    private var isRecording: Boolean = false
    private var currentTakeVideoUrl: String? = null
    private var video_uri: Uri? = null
    private var audioUri: Uri? = null
    private val PERMISSION_CODE: Int = 1234
    private var cameraTimer: Int = CAMERA_TIMER_DEFAULT
    private val avidId = UUID.randomUUID().toString()
    private var mediaPlayer: MediaPlayer? = null

    private var isFullscreen: Boolean = false
    private var speedScroll = 2
    private val handler = Handler(Looper.getMainLooper())
    private val scroll = 5000
    private val runnable = object : Runnable {
        var count = 0
        override fun run() {
            if (count == _binding.recSelfPrompt.adapter?.itemCount) count = 0
            if (count < (_binding.recSelfPrompt.adapter?.itemCount ?: -1)) {
                _binding.recSelfPrompt.smoothScrollToPosition(++count)
                handler.postDelayed(this, scroll.toLong() / speedScroll)
            }
        }
    }

    private val activityResultVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                loadVideoFromStorage(result)
            }
        }

    private val activityAudioResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                loadAudio(result)
            } ?: kotlin.run {
                if (result.resultCode == RESULT_OK) {
                    msg.showShortMsg(getString(R.string.failed_to_load_audio))
                }
            }
        }

    private fun loadAudio(result: ActivityResult) {
        audioUri = Objects.requireNonNull<Intent>(result.data).data
        if (audioUri != null) {
            _binding.music.isChecked = !_binding.music.isChecked
        }
    }

    private fun loadVideoFromStorage(result: ActivityResult) {
        video_uri = Objects.requireNonNull<Intent>(result.data).data
        TrimVideoActivity.startActivity(this, video_uri, avidId, -1, true)
    }

    private val llm: LinearLayoutManager = object : LinearLayoutManager(this, VERTICAL, false) {
        override fun smoothScrollToPosition(
            recyclerView: RecyclerView, state: RecyclerView.State, position: Int
        ) {
            val scroller: LinearSmoothScroller =
                object : LinearSmoothScroller(this@AvidCaptureActivity) {
                    private val MILLISECONDS_PER_INCH = 200f

                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return (MILLISECONDS_PER_INCH / displayMetrics.densityDpi)
                    }
                }
            scroller.targetPosition = position
            startSmoothScroll(scroller)
        }
    }
    var permissionsList: ArrayList<String> = arrayListOf()
    var permissionsCount = 0
    var permissionAskedTime = 0

    private var permissionsLauncherVideo =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsList = ArrayList()
            permissionsCount = 0
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else if (!hasStoragePermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsCount++
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO)) {
                    permissionsList.add(Manifest.permission.READ_MEDIA_VIDEO)
                } else if (!hasStoragePermission(Manifest.permission.READ_MEDIA_VIDEO)) {
                    permissionsCount++
                }
            }
            if (permissionsList.size > 0) {
                //Some permissions are denied and can be asked again.
                if (permissionAskedTime >= 1) {
                    permissionAskedTime = 0
                    return@registerForActivityResult
                }
                askForPermissions(permissionsList)
                permissionAskedTime++
            } else if (permissionsCount > 0) {
                /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
                showPermissionDialog("Please provide access to Video files!")
            } else {
                //All permissions granted. Do your stuff 🤞
                pickVideo()
            }
        }

    private var permissionsLauncherMusic =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsList = ArrayList()
            permissionsCount = 0
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else if (!hasStoragePermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsCount++
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO)) {
                    permissionsList.add(Manifest.permission.READ_MEDIA_AUDIO)
                } else if (!hasStoragePermission(Manifest.permission.READ_MEDIA_AUDIO)) {
                    permissionsCount++
                }
            }
            if (permissionsList.size > 0) {
                //Some permissions are denied and can be asked again.
                if (permissionAskedTime >= 1) {
                    permissionAskedTime = 0
                    return@registerForActivityResult
                }
                askForMusicPermissions(permissionsList)
                permissionAskedTime++
            } else if (permissionsCount > 0) {
                /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
                showPermissionDialog("Please provide access to Music files!")
            } else {
                //All permissions granted. Do your stuff 🤞
                pickAudio()
            }
        }

    private fun askForPermissions(permissionsList: ArrayList<String>) {
        val newPermissionStr = Array(permissionsList.size) {
            permissionsList[it]
        }

        if (newPermissionStr.isNotEmpty()) {
            permissionsLauncherVideo.launch(newPermissionStr)
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showPermissionDialog("Please provide access to Video files!")
        }
    }

    private fun askForMusicPermissions(permissionsList: ArrayList<String>) {
        val newPermissionStr = Array(permissionsList.size) {
            permissionsList[it]
        }

        if (newPermissionStr.isNotEmpty()) {
            permissionsLauncherMusic.launch(newPermissionStr)
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showPermissionDialog("Please provide access to Music files!")
        }
    }

    @SuppressLint("ClickableViewAccessibility", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msg.msgType = MSG_TYPE.SNACKBAR
        msg.updateSnackbarView(_binding.root)
        initCamera()

        resetUi()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true
        _binding.recSelfPrompt.layoutManager = llm

        _binding.button.setVideoDuration(60000)
        _binding.button.enableVideoRecording(true)
        _binding.button.enablePhotoTaking(false)

        viewModel.getAllTakes().observe(this, Observer {
            _binding.takes.isChecked = !CollectionUtils.isEmpty(it)
        })

        _binding.selfPromptBtn.setOnClickListener {
            SelfPromptDialog().showDialog(supportFragmentManager, viewModel.text.value ?: "")
        }

        _binding.galleryImg.setOnClickListener {
            pickVideo()
        }

        _binding.close.setOnClickListener {
            currentTakeVideoUrl?.let { it1 -> VideoUtil.deleteFile(File(it1)) }
            finish()
        }

        _binding.flash.setOnClickListener {
            _binding.jcameraview.flashOn = !_binding.jcameraview.flashOn
            _binding.flash.isChecked = _binding.jcameraview.flashOn
        }

        _binding.cameraSwitch.setOnClickListener {
            _binding.jcameraview.switchCamera()
        }

        _binding.music.setOnClickListener {
            pickAudio()
        }

        _binding.takes.setOnClickListener {
            startActivity(Intent(this, TakesActivity::class.java).putExtra("avidId", avidId))
        }

        _binding.mic.setOnClickListener {
            _binding.mic.isChecked = !_binding.mic.isChecked
            _binding.jcameraview.setMicMute(!_binding.mic.isChecked)
        }

        _binding.btnDone.setOnClickListener {
            _binding.jcameraview.confirmVideo()
            if (currentTakeVideoUrl != null) {
                if (audioUri == null) {
                    val firstFrame =
                        ExtractVideoInfoUtil(Uri.parse(currentTakeVideoUrl)).extractFrame()
                    viewModel.addTakeToDb(currentTakeVideoUrl!!, firstFrame, avidId)
                    _binding.jcameraview.onPause()
                    TrimVideoActivity.startActivity(
                        this@AvidCaptureActivity, Uri.parse(currentTakeVideoUrl), avidId, -1, false
                    )
                    resetUi()
                } else {
                    //merge audio to video
                    currentTakeVideoUrl?.let {
                        if (!File(it).exists()) {
                            currentTakeVideoUrl =
                                cacheDir.path + File.separator + currentTakeVideoUrl
                            if (File(it).exists()) {
                                startMerge()
                            }
                        } else {
                            startMerge()
                        }
                    }
                }
            } else msg.showShortMsg("Failed to get file path!")
        }

        _binding.btnRetake.setOnClickListener {
            currentTakeVideoUrl?.let { it1 -> VideoUtil.deleteFile(File(it1)) }
            _binding.jcameraview.resetState(JCameraView.TYPE_VIDEO)
            resetUi()
        }

        _binding.timer.setOnClickListener {
            _binding.timer.setBackgroundResource(
                when (cameraTimer) {
                    CAMERA_TIMER_DEFAULT -> {
                        cameraTimer = CAMERA_TIMER_3S
                        R.drawable.timer_selector_3s
                    }
                    CAMERA_TIMER_3S -> {
                        cameraTimer = CAMERA_TIMER_5S
                        R.drawable.timer_selector_5s
                    }
                    CAMERA_TIMER_5S -> {
                        cameraTimer = CAMERA_TIMER_8S
                        R.drawable.timer_selector_8s
                    }
                    CAMERA_TIMER_8S -> {
                        cameraTimer = CAMERA_TIMER_DEFAULT
                        R.drawable.timer_selector_0s
                    }
                    else -> R.drawable.timer_selector_0s
                }
            )
        }

        _binding.speed1x.setOnClickListener {
            _binding.speed1x.isChecked = !_binding.speed1x.isChecked
            if (_binding.speed1x.isChecked) {
                speedScroll = 1
            }
            _binding.speed2x.isChecked = false
            _binding.speed3x.isChecked = false
        }
        _binding.speed2x.setOnClickListener {
            _binding.speed2x.isChecked = !_binding.speed2x.isChecked
            if (_binding.speed2x.isChecked) {
                speedScroll = 2
            }

            _binding.speed3x.isChecked = false
            _binding.speed1x.isChecked = false
        }
        _binding.speed3x.setOnClickListener {
            _binding.speed3x.isChecked = !_binding.speed3x.isChecked
            if (_binding.speed3x.isChecked) {
                speedScroll = 3
            }
            _binding.speed2x.isChecked = false
            _binding.speed1x.isChecked = false
        }

        _binding.button.actionListener = object : CameraVideoButton.ActionListener {
            @SuppressLint("MissingPermission")
            override fun onStartRecord() {
                lastTakeRecordTime = 0
                _binding.jcameraview.startRecord()
                startOrResumeMusic()
            }

            override fun onEndRecord(time: Long) {
                lastTakeRecordTime = time - 1000
                _binding.jcameraview.stopRecord(lastTakeRecordTime)
                isRecording = false
                disableButton(false)
                handler.removeCallbacks(runnable)
                afterTakeUI()
            }

            override fun onDurationTooShortError() {
                isRecording = false
                newTakeUI()
                _binding.gallery.isVisible = true
                _binding.selfPromptBtn.isVisible = true
                disableButton(false)
            }

            override fun onSingleTap() {
                if (isTakeDone) {
                    recreate()
                    return
                }
                if (!isRecording) {
                    if (cameraTimer == CAMERA_TIMER_DEFAULT) {
                        disableButton(true)
                        _binding.button.toggleRecording()
                        handler.postDelayed(runnable, scroll.toLong())
                    } else {
                        _binding.button.isEnabled = false
                        disableButton(true)
                        _binding.timerView.start {
                            timerDuration(cameraTimer / 1000)
                            textSize(50)
                            textColor(
                                ContextCompat.getColor(
                                    this@AvidCaptureActivity, R.color.colorPrimary
                                )
                            )
                            timerAnimation(ScaleAnimation(this@AvidCaptureActivity, null))
                            shape(Shape.CIRCLE)
                        }
                        _binding.timerView.onFinished {
                            _binding.button.toggleRecording()
                            handler.postDelayed(runnable, scroll.toLong())
                            _binding.button.isEnabled = true
                        }
                    }
                } else {
                    _binding.button.stopButtonAnim()
                    disableButton(false)
                    handler.removeCallbacks(runnable)
                }
            }

            override fun onCancelled() {

            }
        }

        viewModel.text.observe(this, Observer {
            // Prepare the measured text
            val finalString = if (it.count { c -> c == '\n' } > 1) {
                it
            } else {
                val displayMetrics = DisplayMetrics()
                val width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    windowManager.currentWindowMetrics.bounds.right
                } else {
                    windowManager.defaultDisplay.getMetrics(displayMetrics)
                    displayMetrics.widthPixels
                }

                val bounds = Rect()
                val textPaint = Paint().apply {
                    textSize = resources.getDimension(R.dimen.sp_16)
                }
                textPaint.getTextBounds("W", 0, 1, bounds)
                val widthOfTxt = bounds.right - bounds.left
                val maxChar = width / widthOfTxt
                val sb = StringBuilder()
                var spaceIndex = 0
                for (i in it.indices) {
                    if (i > 0 && i % maxChar == 0) {
                        if (it[i] == StringUtils.SPACE[0]) {
                            sb.append("\n")
                        } else {
                            if (spaceIndex > 0) {
                                sb[spaceIndex] = '\n'
                            }
                        }
                    }
                    if (it[i] == StringUtils.SPACE[0]) {
                        spaceIndex = i
                    }
                    sb.append(it[i])
                }

                sb.toString()
            }

            val list = finalString.split("\n")
            _binding.speedLayout.isVisible = it.isNotEmpty() && list.isNotEmpty()
            _binding.selfPromptBtn.isChecked = StringUtils.isNotEmpty(it)
            if (it.isNotEmpty() && list.isNotEmpty()) {
                val counter = AtomicInteger(0)
                val selfPromptData = list.stream().filter { s ->
                    return@filter s.isNotEmpty()
                }.collect(Collectors.groupingBy { counter.getAndIncrement() / 2 }).values.stream()
                    .collect(Collectors.toList()).stream().map { m ->
                        StringUtils.join(m, "\n")
                    }.collect(Collectors.toList())
                selfPromptData.add(0, "")
                selfPromptData.add("")
                selfPromptData.add("")
                if (_binding.recSelfPrompt.adapter == null) {
                    _binding.recSelfPrompt.adapter = SelfPromptTextAdapter(selfPromptData)
                } else {
                    (_binding.recSelfPrompt.adapter as SelfPromptTextAdapter).updateList(
                        selfPromptData
                    )
                }

                _binding.recSelfPrompt.isVisible = true
            } else {
                (_binding.recSelfPrompt.adapter as? SelfPromptTextAdapter)?.clearList()
                _binding.recSelfPrompt.isVisible = false
            }
        })

        _binding.recSelfPrompt.addOnScrollListener(scrollListener)
    }

    private fun resetUi() {
        //enable all btns
        _binding.button.isEnabled = true
        _binding.timer.isEnabled = true
        _binding.cameraSwitch.isEnabled = true
        _binding.mic.isEnabled = true
        _binding.music.isEnabled = true
        _binding.takes.isEnabled = true

        //change state
        _binding.flash.isChecked = false
        _binding.timer.setBackgroundResource(R.drawable.timer_selector_0s)
        _binding.mic.isChecked = true
        _binding.music.isChecked = false
        _binding.selfPromptBtn.isChecked = false
        _binding.gallery.isVisible = true
        _binding.recSelfPrompt.adapter = null

        _binding.close.isVisible = true
        _binding.recSelfPrompt.isVisible = false
        _binding.selfPromptBtn.isVisible = true
        _binding.speedLayout.isVisible = false

        //data reset
        viewModel.setText("")
        currentTakeVideoUrl = null
        lastTakeRecordTime = 0
        audioUri = null
        video_uri = null

        newTakeUI()
    }

    private fun startMerge() {
        _binding.btnDone.isEnabled = false
        val outPath = cacheDir.path + File.separator + "merge"
        NormalProgressDialog.showLoading(this, "Merging Audio and Video", false)
        Observable.just(currentTakeVideoUrl).subscribeOn(Schedulers.io()).switchMap { url ->
            val fileName = "m_vid_${System.currentTimeMillis()}.mp4"
            val outputDir = File(outPath)
            if (!outputDir.exists() && outputDir.mkdirs()) {
                Log.d(TAG, "Directory created: $outputDir")
            }
            val outFile = File(outPath + File.separator + fileName)
            outFile.createNewFile()
            val filePath = FileUtils.getPath(this, audioUri!!)
            val b = if (filePath?.endsWith("m4a", true) == true) {
                VideoUtil.muxM4AMp4(
                    FileUtils.getPath(
                        this, audioUri!!
                    ), url, outFile.path
                )
            } else if (filePath?.endsWith("aac", true) == true) {
                VideoUtil.muxAacMp4(
                    FileUtils.getPath(
                        this, audioUri!!
                    ), url, outFile.path
                )
            } else {
                return@switchMap Observable.just(filePath).flatMap {
                    val path = it.trim()
                    val tmpAudio = File(cacheDir.absolutePath, "tmp.m4a")
                    tmpAudio.delete()
                    val session = AudioTrackToAacConvertor.convertAudio(
                        path, tmpAudio.absolutePath, maxDurationMillis = lastTakeRecordTime
                    )
                    if (ReturnCode.isSuccess(session?.returnCode)) {
                        // SUCCESS
                        Log.d(
                            TAG, java.lang.String.format(
                                "FFmpeg process exited with state %s and rc %s.%s",
                                session?.state,
                                session?.returnCode,
                                session?.failStackTrace
                            )
                        )
                        Observable.just(tmpAudio.absolutePath)
                    } else if (ReturnCode.isCancel(session?.returnCode)) {
                        // CANCEL
                        tmpAudio.delete()
                        Observable.empty()
                    } else {
                        // FAILURE
                        Log.d(
                            TAG, String.format(
                                "Command failed with state %s and rc %s.%s",
                                session?.state,
                                session?.returnCode,
                                session?.failStackTrace
                            )
                        )
                        tmpAudio.delete()
                        Observable.error(Throwable(session?.failStackTrace))
                    }
                }.flatMap {
                    val a = VideoUtil.muxM4AMp4(
                        it, url, outFile.path
                    )
                    VideoUtil.deleteFile(File(it))
                    if (a) {
                        Observable.just(outFile.path)
                    } else {
                        Observable.error(Exception("Failed to mux!!"))
                    }
                }
            }

            if (b) {
                Observable.just(outFile.path)
            } else {
                Observable.error(Throwable(Exception("Failed to merge the audio with video file!")))
            }
        }.flatMap { mergedUri ->
            // trim Audio To Video length
            val fileName = "m_trimmed_vid_${System.currentTimeMillis()}.mp4"
            val file = File(outPath + File.separator + fileName)
            file.createNewFile()
//            VideoUtil.cutVideo(mergedUri, file.path, 0.0, lastTakeRecordTime.toDouble()).flatMap {
//                //delete the trimmed video
//                VideoUtil.deleteFile(File(mergedUri))
//                Observable.just(it)
//            }
            val session =
                VideoFFMpegUtil.trimVideo(mergedUri, file.path, 0, lastTakeRecordTime)
            if (ReturnCode.isSuccess(session?.returnCode)) {
                // SUCCESS
                Log.d(
                    "AvidCaptureActivity", java.lang.String.format(
                        "FFmpeg process exited with state %s and rc %s.%s",
                        session?.state,
                        session?.returnCode,
                        session?.failStackTrace
                    )
                )
                Observable.just(file.path)
            } else if (ReturnCode.isCancel(session?.returnCode)) {
                // CANCEL
                Observable.empty()
            } else {
                // FAILURE
                Log.d(
                    "AvidCaptureActivity", String.format(
                        "Command failed with state %s and rc %s.%s",
                        session?.state,
                        session?.returnCode,
                        session?.failStackTrace
                    )
                )
                Observable.error(Throwable(session?.failStackTrace))
            }
        }.flatMap {
            //delete video
            VideoUtil.deleteFile(File(currentTakeVideoUrl!!))
            Observable.just(it)
        }.flatMap {
            val firstFrame = ExtractVideoInfoUtil(Uri.parse(it)).extractFrame()
            viewModel.addTakeToDb(it, firstFrame, avidId)
            Observable.just(it)
        }.observeOn(AndroidSchedulers.mainThread()).subscribe({
            TrimVideoActivity.startActivity(
                this@AvidCaptureActivity, Uri.parse(it), avidId, -1, false
            )
            _binding.btnDone.isEnabled = true
        }, {
            NormalProgressDialog.stopLoading(this)
            Log.e(TAG, "startMerge: ", it)
            _binding.btnDone.isEnabled = true
            resetUi()
        }, {
            NormalProgressDialog.stopLoading(this)
            _binding.btnDone.isEnabled = true
            resetUi()
        }).let {
            viewModel.compositeDisposable.add(it)
        }
    }

    private fun startOrResumeMusic() {
        mediaPlayer?.apply {
            audioUri?.let {
                setDataSource(applicationContext, it)
                prepare()
                start()
            }
        }
    }

    private fun stopMusic() {
        mediaPlayer?.apply {
            audioUri?.let {
                if (isPlaying) {
                    reset()
                }
            }
        }
    }

    private fun pickAudio() {
        if (hasStoragePermission(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_AUDIO
                else Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            activityAudioResult.launch(
                intent
            )
        } else {
            permissionsLauncherMusic.launch(
                arrayOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        Manifest.permission.READ_MEDIA_AUDIO
                    else Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun disableButton(isDisabled: Boolean) {
        _binding.button.isEnabled = !isDisabled
        _binding.selfPromptBtn.isVisible = false
        _binding.gallery.isVisible = false
        _binding.close.isVisible = !isDisabled
        _binding.timer.isEnabled = !isDisabled
        _binding.cameraSwitch.isEnabled = !isDisabled
        _binding.mic.isEnabled = !isDisabled
        _binding.music.isEnabled = !isDisabled
        _binding.takes.isEnabled = !isDisabled
    }

    private fun initCamera() {
        //Set video save path
        _binding.jcameraview.setSaveVideoPath(
            Environment.getExternalStorageDirectory().path + File.separator + "videoeditor" + File.separator + "tex_video"
        )
        _binding.jcameraview.setMinDuration(2000) //Set minimum recording duration
        _binding.jcameraview.setDuration(60000) //Set the maximum recording time
        _binding.jcameraview.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH)
        _binding.jcameraview.setMicMute(false)
        _binding.jcameraview.setErrorLisenter(object : ErrorListener {
            override fun onError() {
                //error monitoring
                Log.d("CJT", "camera error")
                val intent = Intent()
                setResult(103, intent)
                finish()
            }

            override fun AudioPermissionError() {
                msg.showLongMsg("Can you give me permission to record?")
            }
        })
        //JCameraView monitor
        _binding.jcameraview.setJCameraLisenter(object : JCameraListener {
            override fun captureSuccess(bitmap: Bitmap) {
                //Get picture bitmap
//                val path = FileUtil.saveBitmap("tex_video", bitmap)
            }

            override fun recordSuccess(url: String, firstFrame: Bitmap) {
                //Get video path
                currentTakeVideoUrl = url
                isRecording = false
                disableButton(false)
                handler.removeCallbacks(runnable)
            }
        })
        _binding.jcameraview.setRecordStateListener(object : RecordStateListener {
            override fun recordStart() {
                isRecording = true
                _binding.close.isVisible = false
            }

            override fun recordEnd(time: Long) {
                _binding.close.isVisible = false
                afterTakeUI()
                stopMusic()
                isRecording = false
                Log.e("Recording status callback", "recording time：$time")
                disableButton(false)
                handler.removeCallbacks(runnable)
                isTakeDone = true
            }

            override fun recordCancel() {
                isRecording = false
                stopMusic()
            }
        })
    }

    private fun afterTakeUI() {
        _binding.close.isVisible = true
        _binding.gallery.isVisible = false
        _binding.selfPromptBtn.isVisible = false
        _binding.recSelfPrompt.isVisible = false
        _binding.llSide.isVisible = false
        _binding.afterTakeLayout.isVisible = true
        _binding.button.isInvisible = true
        _binding.speedLayout.isVisible = false
    }

    private fun newTakeUI() {
        _binding.close.isVisible = true
        _binding.recSelfPrompt.isVisible = true
        _binding.llSide.isVisible = true
        _binding.afterTakeLayout.isVisible = false
        _binding.button.isVisible = true
        isTakeDone = false
    }

    private fun pickVideo() {
        if (hasStoragePermission(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_VIDEO else
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            activityResultVideo.launch(
                intent
            )
        } else {
            permissionsLauncherVideo.launch(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    arrayOf(Manifest.permission.READ_MEDIA_VIDEO) else
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }

    private fun showPermissionDialog(subtitle: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Required!")
        builder.setMessage(subtitle)
        builder.setPositiveButton("Allow") { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    override fun onResume() {
        super.onResume()
        _binding.jcameraview.onResume()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        _binding.jcameraview.onPause()
    }

    override fun onStart() {
        super.onStart()
        _binding.timerView.cancel()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
        _binding.timerView.cancel()
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        NormalProgressDialog.stopLoading(this)
        _binding.recSelfPrompt.removeOnScrollListener(scrollListener)
        resetUi()
//        _binding.jcameraview.onDestroyCamera()
        super.onDestroy()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            synchronized(this) {
                val i =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if ((recyclerView.adapter as? SelfPromptTextAdapter)?.selectedPos != i - 1) {
                    (recyclerView.adapter as? SelfPromptTextAdapter)?.selectedPos = i - 1
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300

        /**
         * Camera recording timer zero
         */
        private const val CAMERA_TIMER_DEFAULT = 0

        /**
         * Camera recording timer 3 seconds
         */
        private const val CAMERA_TIMER_3S = 3000

        /**
         * Camera recording timer 5 seconds
         */
        private const val CAMERA_TIMER_5S = 5000

        /**
         * Camera recording timer 8 seconds
         */
        private const val CAMERA_TIMER_8S = 8000
    }

}
