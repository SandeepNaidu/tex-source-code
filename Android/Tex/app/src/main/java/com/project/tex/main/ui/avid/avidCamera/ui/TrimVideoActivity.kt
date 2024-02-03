package com.project.tex.main.ui.avid.avidCamera.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthenica.ffmpegkit.ReturnCode
import com.project.tex.R
import com.project.tex.addmedia.FileUtils
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityTrimVideoBinding
import com.project.tex.main.ui.avid.avidCamera.adapter.TrimVideoAdapter
import com.project.tex.main.ui.avid.avidCamera.model.VideoEditInfo
import com.project.tex.main.ui.avid.avidCamera.utils.*
import com.project.tex.main.ui.avid.avidCamera.view.NormalProgressDialog
import com.project.tex.main.ui.avid.avidCamera.view.RangeSeekBar
import com.project.tex.main.ui.avid.avidCamera.view.VideoThumbSpacingItemDecoration
import com.project.tex.main.ui.search.AvidViewModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.properties.Delegates

class TrimVideoActivity : BaseActivity<ActivityTrimVideoBinding, AvidViewModel>() {
    override fun getViewBinding() = ActivityTrimVideoBinding.inflate(layoutInflater)

    override fun getViewModelInstance() =
        ViewModelProvider(this).get(AvidViewModel::class.java)

    private lateinit var mExtractVideoInfoUtil: ExtractVideoInfoUtil
    private var mMaxWidth = 0 //The maximum width of the clippable area
    private var duration: Long = 0 //total video duration
    private var videoEditAdapter: TrimVideoAdapter? = null
    private var averageMsPx = 0f //px per millisecond
    private var averagePxMs = 0f //ms milliseconds per px
    private var OutPutFileDirPath: String? = null
    private var mExtractFrameWorkThread: ExtractFrameWorkThread? = null
    private var leftProgress: Long = 0
    private var rightProgress: Long = 0

    //Crop the time position of the left area of the video,
    // and the time position of the right side
    private var scrollPos: Long = 0
    private var mScaledTouchSlop = 0
    private var lastScrollX = 0
    private var isSeeking = false
    private lateinit var mVideoPath: Uri
    private var isFromGallery by Delegates.notNull<Boolean>()
    private var mOriginalWidth = 0 //video original width
    private var mOriginalHeight = 0 //video original height
    private var mSurfaceTexture: SurfaceTexture? = null
    private val mMediaPlayer: MediaPlayer = MediaPlayer()

    //    private var mMp4Composer: Mp4Composer? = null
    private lateinit var seekBar: RangeSeekBar
    private lateinit var avidId: String
    private var avidTakeId: Int? = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        initView()
    }

    protected fun init() {
        avidId = intent.getStringExtra("avidId") ?: ""
        avidTakeId = intent.getIntExtra("avidTakeId", -1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mVideoPath = intent.getParcelableExtra("videoPath", Uri::class.java)!!
        } else {
            mVideoPath = intent.getParcelableExtra("videoPath")!!
        }
        isFromGallery = intent.getBooleanExtra("isFromGallery", false)
        _binding.topDownLayout.isVisible = !isFromGallery
//        if (!File(mVideoPath.toString()).exists()) {
//            mVideoPath = Uri.fromFile(File(mVideoPath.toString()))
//        }/storage/emulated/0/videoeditor/tex_video/video_1675192119616.mp4
        if (FileUtils.getPath(this, mVideoPath)?.let { File(it).exists() } == false) {
            val avidFile = File(cacheDir, mVideoPath.toString())
            if (!avidFile.exists()) {
                msg.showShortMsg("File does not exists!")
                hideUI()
                return
            } else {
                mVideoPath = Uri.fromFile(avidFile)
            }
        }
        mExtractVideoInfoUtil = ExtractVideoInfoUtil(mVideoPath)
        mMaxWidth = UIUtils.getScreenWidth() - MARGIN * 2
        mScaledTouchSlop = ViewConfiguration.get(this).scaledTouchSlop
        Observable.create<String> { e ->
            e.onNext(mExtractVideoInfoUtil.videoLength)
            e.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    subscribe(d)
                }

                override fun onNext(s: String) {
                    duration = java.lang.Long.valueOf(mExtractVideoInfoUtil.videoLength)
                    //Correct the problem that the obtained video duration is not an integer
                    val tempDuration = (duration / 1000f).toDouble()
                    duration = (BigDecimal(tempDuration).setScale(0, RoundingMode.HALF_UP)
                        .toInt() * 1000).toLong()
                    Log.e(TAG, "total video duration：$duration")
                    initEditVideo()
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    private fun hideUI() {
        _binding.llTrimContainer.isVisible = false
        _binding.saveLocal.isVisible = false
    }

    protected fun initView() {
        _binding.videoThumbListview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        videoEditAdapter = TrimVideoAdapter(this, mMaxWidth / MAX_COUNT_RANGE)
        _binding.videoThumbListview.adapter = videoEditAdapter
        _binding.videoThumbListview.addOnScrollListener(mOnScrollListener)
        _binding.glsurfaceview.init { surfaceTexture: SurfaceTexture? ->
            mSurfaceTexture = surfaceTexture
            initMediaPlayer(surfaceTexture)
        }

        _binding.btnDone.setOnClickListener {
            trimmerVideo()
        }

        _binding.btnDiscard.setOnClickListener {
            deleteTake()
        }

        _binding.close.setOnClickListener {
            finish()
        }

        _binding.saveLocal.setOnClickListener {
            Observable.just(mVideoPath).subscribeOn(Schedulers.io()).flatMap { uri ->
                val file = FileUtils.getPath(this, uri)?.let { it1 ->
                    File(it1)
                } ?: kotlin.run {
                    File(cacheDir, uri.toString())
                }.copyTo(
                    File(Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_MOVIES),
                    false
                )
                if (file.exists()) {
                    Observable.just(
                        file
                    )
                } else {
                    Observable.empty()
                }
            }.flatMap {
                viewModel.deleteFileFromDB()
                Observable.just(it)
            }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    msg.showShortMsg("File saved at ${file.absolutePath}")
                }, { t ->
                    msg.showLongMsg("File save failed: $t")
                })?.let {
                    viewModel.compositeDisposable.add(it)
                }

        }
        _binding.deleteTake.setOnClickListener {
            deleteTake()
        }
    }

    private fun deleteTake() {
        if (avidTakeId != -1) {
            avidTakeId?.let { it1 ->
                viewModel.deleteAvid(it1).subscribe(
                    {
                        msg.showShortMsg("Take Deleted!")
                        deleteF()
                        setResult(RESULT_OK, Intent().putExtra("isDeleted", true))
                        finish()
                    },
                    { t ->
                        Log.e(TAG, "deleteTake: ", t)
                    }
                ).let {
                    viewModel.compositeDisposable.add(it)
                }
            }
        } else {
            avidId.let { it1 ->
                viewModel.deleteAvidByAvidId(it1).subscribe(
                    {
                        msg.showShortMsg("Take Deleted!")
                        deleteF()
                        setResult(RESULT_OK, Intent().putExtra("isDeleted", true))
                        finish()
                    },
                    { t ->
                        Log.e(TAG, "deleteTake: ", t)
                    }
                ).let {
                    viewModel.compositeDisposable.add(it)
                }
            }
        }
    }

    private fun deleteF() {
        try {
            FileUtils.getPath(this, mVideoPath)?.let { File(it).delete() }
                ?: kotlin.run {
                    File(cacheDir, mVideoPath.toString()).delete()
                }
        } catch (e: Exception) {
            Log.e(TAG, "deleteF: ", e)
        }
    }

    private fun initEditVideo() {
        //for video edit
        val startPosition: Long = 0
        val endPosition = duration
        val thumbnailsCount: Int
        val rangeWidth: Int
        val isOver_60_s: Boolean
        if (endPosition <= MAX_CUT_DURATION) {
            isOver_60_s = false
            thumbnailsCount = MAX_COUNT_RANGE
            rangeWidth = mMaxWidth
        } else {
            isOver_60_s = true
            thumbnailsCount = (endPosition * 1.0f / (MAX_CUT_DURATION * 1.0f)
                    * MAX_COUNT_RANGE).toInt()
            rangeWidth = mMaxWidth / MAX_COUNT_RANGE * thumbnailsCount
        }
        _binding.videoThumbListview
            .addItemDecoration(VideoThumbSpacingItemDecoration(MARGIN, thumbnailsCount))

        //init seekBar
        if (isOver_60_s) {
            seekBar = RangeSeekBar(this, 0L, MAX_CUT_DURATION)
            seekBar.selectedMinValue = 0L
            seekBar.selectedMaxValue = MAX_CUT_DURATION
        } else {
            seekBar = RangeSeekBar(this, 0L, endPosition)
            seekBar.selectedMinValue = 0L
            seekBar.selectedMaxValue = endPosition
        }
        seekBar.setMin_cut_time(MIN_CUT_DURATION) //Set minimum cropping time
        seekBar.isNotifyWhileDragging = true
        seekBar.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener)
        _binding.idSeekBarLayout.addView(seekBar)
//        Log.d(TAG, "-------thumbnailsCount--->>>>$thumbnailsCount")
        averageMsPx = duration * 1.0f / rangeWidth * 1.0f
//        Log.d(TAG, "-------rangeWidth--->>>>$rangeWidth")
//        Log.d(TAG, "-------localMedia.getDuration()--->>>>$duration")
//        Log.d(TAG, "-------averageMsPx--->>>>$averageMsPx")
        OutPutFileDirPath = VideoUtil.getSaveEditThumbnailDir(this)
        val extractW = mMaxWidth / MAX_COUNT_RANGE
        val extractH: Int = UIUtils.dp2Px(64)
        mExtractFrameWorkThread = ExtractFrameWorkThread(
            extractW, extractH, mUIHandler,
            mVideoPath,
            OutPutFileDirPath, startPosition, endPosition, thumbnailsCount
        )
        mExtractFrameWorkThread?.start()

        //init pos icon start
        leftProgress = 0
        rightProgress = if (isOver_60_s) {
            MAX_CUT_DURATION
        } else {
            endPosition
        }
//        _binding.videoShootTip.text = String.format("Cut out %d s", rightProgress / 1000)
        averagePxMs = mMaxWidth * 1.0f / (rightProgress - leftProgress)
        Log.d(TAG, "------averagePxMs----:>>>>>$averagePxMs")
    }

    /**
     * 初始化MediaPlayer
     */
    private fun initMediaPlayer(surfaceTexture: SurfaceTexture?) {
        try {
            mMediaPlayer.setDataSource(this, mVideoPath)
            val surface = Surface(surfaceTexture)
            mMediaPlayer.setSurface(surface)
            surface.release()
            mMediaPlayer.isLooping = true
            mMediaPlayer.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                override fun onPrepared(mp: MediaPlayer) {
                    val lp: ViewGroup.LayoutParams = _binding.glsurfaceview.layoutParams
                    val videoWidth: Int = mp.videoWidth
                    val videoHeight: Int = mp.videoHeight
                    val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
                    val screenWidth: Int = _binding.layoutSurfaceView.width
                    val screenHeight: Int = _binding.layoutSurfaceView.height
                    val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()
                    if (videoProportion > screenProportion) {
                        lp.width = screenWidth
                        lp.height = (screenWidth.toFloat() / videoProportion).toInt()
                    } else {
                        lp.width = (videoProportion * screenHeight.toFloat()).toInt()
                        lp.height = screenHeight
                    }
                    _binding.glsurfaceview.layoutParams = lp
                    mOriginalWidth = videoWidth
                    mOriginalHeight = videoHeight
                    Log.e("videoView", "videoWidth:$videoWidth, videoHeight:$videoHeight")

                    mp.start()
                    //Set MediaPlayer's OnSeekComplete listener
                    mp.setOnSeekCompleteListener {
                        Log.d(TAG, "------ok----real---start-----")
                        Log.d(TAG, "------isSeeking-----$isSeeking")
                        if (!isSeeking) {
                            videoStart()
                        }
                    }
                }
            })
            mMediaPlayer.prepare()
            videoStart()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mMediaPlayer.setOnErrorListener { mp, what, extra ->
            Log.d(TAG, "initMediaPlayer: $what $extra")
            return@setOnErrorListener true
        }
    }

    /**
     * 视频裁剪
     */
    private fun trimmerVideo() {
        NormalProgressDialog
            .showLoading(this, resources.getString(R.string.in_process), false)
        videoPause()
        Log.e(
            TAG, "trimVideo...startSecond:" + leftProgress + ", endSecond:"
                    + rightProgress
        )

        val outFile = VideoUtil.getTrimmedVideoPath(
            this, "tex_video/trimmedVideo",
            "trimmedVideo_"
        )

        FileUtils.getPath(this, mVideoPath)?.let {
            Observable.just(it).subscribeOn(Schedulers.io())
                .flatMap {
                    val session = VideoFFMpegUtil.trimVideo(
                        it,
                        outFile,
                        leftProgress,
                        rightProgress
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
                        Observable.just(outFile)
                    } else if (ReturnCode.isCancel(session?.returnCode)) {
                        // CANCEL
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
                        Observable.error(Throwable(session?.failStackTrace))
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {
                        subscribe(d)
                    }

                    override fun onNext(outputPath: String) {
                        // /storage/emulated/0/Android/data/com.kangoo.diaoyur/files/small_video/trimmedVideo_20180416_153217.mp4
                        Log.e(TAG, "cutVideo---onSuccess")
                        NormalProgressDialog.stopLoading()
                        try {
//                        compressVideo(outputPath)
//                        startMediaCodec(outputPath)
                            startActivity(
                                Intent(this@TrimVideoActivity, AvidPreviewActivity::class.java)
                                    .putExtra("videoPath", Uri.parse(outputPath))
                                    .putExtra("avidId", avidId)
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        Log.e(TAG, "cutVideo---onError:$e")
                        NormalProgressDialog.stopLoading()
                        msg.showShortMsg("Video cropping failed")
                    }

                    override fun onComplete() {}
                })
        }
//        VideoUtil.cutVideo(
//            FileUtils.getPath(this, mVideoPath),
//            VideoUtil.getTrimmedVideoPath(
//                this, "tex_video/trimmedVideo",
//                "trimmedVideo_"
//            ),
//            (leftProgress / 1000).toDouble(),
//            (rightProgress / 1000).toDouble()
//        )
    }

    private fun subscribe(d: Disposable) {

    }

    private var isOverScaledTouchSlop = false
    private val mOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "-------newState:>>>>>$newState")
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isSeeking = false
                    //                videoStart();
                } else {
                    isSeeking = true
                    if (isOverScaledTouchSlop) {
                        videoPause()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isSeeking = false
                val scrollX = scrollXDistance
                //Cannot reach sliding distance
                if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
                    isOverScaledTouchSlop = false
                    return
                }
                isOverScaledTouchSlop = true
                Log.d(TAG, "-------scrollX:>>>>>$scrollX")
                //Initial state, why? Because there is a 56dp blank by default!
                if (scrollX == -MARGIN) {
                    scrollPos = 0
                } else {
                    // why 在这里处理一下,因为onScrollStateChanged早于onScrolled回调
                    videoPause()
                    isSeeking = true
                    scrollPos = (averageMsPx * (MARGIN + scrollX)).toLong()
                    Log.d(TAG, "-------scrollPos:>>>>>$scrollPos")
                    leftProgress = seekBar.selectedMinValue + scrollPos
                    rightProgress = seekBar.selectedMaxValue + scrollPos
                    Log.d(TAG, "-------leftProgress:>>>>>$leftProgress")
                    mMediaPlayer.seekTo(leftProgress.toInt())
                }
                lastScrollX = scrollX
            }
        }

    /**
     * how many px to slide horizontally
     *
     * @return int px
     */
    private val scrollXDistance: Int
        get() {
            val layoutManager: LinearLayoutManager =
                _binding.videoThumbListview.layoutManager as LinearLayoutManager
            val position: Int = layoutManager.findFirstVisibleItemPosition()
            val firstVisibleChildView: View = layoutManager.findViewByPosition(position)!!
            val itemWidth = firstVisibleChildView.width
            return position * itemWidth - firstVisibleChildView.left
        }
    private var animator: ValueAnimator? = null
    private fun anim() {
        Log.d(TAG, "--anim--onProgressUpdate---->>>>>>>" + mMediaPlayer.currentPosition)
        if (_binding.positionIcon.visibility == View.GONE) {
            _binding.positionIcon.visibility = View.VISIBLE
        }
        val params: FrameLayout.LayoutParams = _binding.positionIcon
            .layoutParams as FrameLayout.LayoutParams
        val start = (MARGIN
                + (leftProgress /*mVideoView.getCurrentPosition()*/ - scrollPos) * averagePxMs).toInt()
        val end = (MARGIN + (rightProgress - scrollPos) * averagePxMs).toInt()
        animator = ValueAnimator
            .ofInt(start, end)
            .setDuration(
                rightProgress - scrollPos - (leftProgress /*mVideoView.getCurrentPosition()*/
                        - scrollPos)
            )
        animator!!.interpolator = LinearInterpolator()
        animator!!.addUpdateListener { animation ->
            params.leftMargin = animation.animatedValue as Int
            _binding.positionIcon.layoutParams = params
        }
        animator!!.start()
    }

    private val mUIHandler = MainHandler(this)

    private class MainHandler(activity: TrimVideoActivity) : Handler() {
        private val mActivity: WeakReference<TrimVideoActivity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (activity != null) {
                if (msg.what == ExtractFrameWorkThread.MSG_SAVE_SUCCESS) {
                    if (activity.videoEditAdapter != null) {
                        val info: VideoEditInfo = msg.obj as VideoEditInfo
                        activity.videoEditAdapter?.addItemVideoInfo(info)
                    }
                }
            }
        }
    }

    private val mOnRangeSeekBarChangeListener: RangeSeekBar.OnRangeSeekBarChangeListener =
        RangeSeekBar.OnRangeSeekBarChangeListener { bar, minValue, maxValue, action, isMin, pressedThumb ->
            Log.d(TAG, "-----minValue----->>>>>>$minValue")
            Log.d(TAG, "-----maxValue----->>>>>>$maxValue")
            leftProgress = minValue + scrollPos
            rightProgress = maxValue + scrollPos
            Log.d(TAG, "-----leftProgress----->>>>>>$leftProgress")
            Log.d(TAG, "-----rightProgress----->>>>>>$rightProgress")
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "-----ACTION_DOWN---->>>>>>")
                    isSeeking = false
                    videoPause()
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d(TAG, "-----ACTION_MOVE---->>>>>>")
                    isSeeking = true
                    mMediaPlayer.seekTo((if (pressedThumb == RangeSeekBar.Thumb.MIN) leftProgress else rightProgress).toInt())
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "-----ACTION_UP--leftProgress--->>>>>>$leftProgress")
                    isSeeking = false
                    //Start playing from minValue
                    mMediaPlayer.seekTo(leftProgress.toInt())
                    videoStart();
                }
                else -> {}
            }
        }

    private fun videoStart() {
        Log.d(TAG, "----videoStart----->>>>>>>")
        mMediaPlayer.start()
        _binding.positionIcon.clearAnimation()
        if (animator != null && animator?.isRunning == true) {
            animator!!.cancel()
        }
        anim()
        handler.removeCallbacks(run)
        handler.post(run)
    }

    private fun videoProgressUpdate() {
        val currentPosition: Long = mMediaPlayer.currentPosition.toLong()
        Log.d(TAG, "----onProgressUpdate-cp---->>>>>>>$currentPosition")
        if (currentPosition >= rightProgress) {
            mMediaPlayer.seekTo(leftProgress.toInt())
            _binding.positionIcon.clearAnimation()
            if (animator != null && animator!!.isRunning) {
                animator!!.cancel()
            }
            anim()
        }
    }

    private fun videoPause() {
        isSeeking = false
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.pause()
            handler.removeCallbacks(run)
        }
        Log.d(TAG, "----videoPause----->>>>>>>")
        if (_binding.positionIcon.visibility == View.VISIBLE) {
            _binding.positionIcon.visibility = View.GONE
        }
        _binding.positionIcon.clearAnimation()
        if (animator != null && animator!!.isRunning) {
            animator!!.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        mMediaPlayer.seekTo(leftProgress.toInt())
    }

    override fun onPause() {
        super.onPause()
        videoPause()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val run: Runnable = object : Runnable {
        override fun run() {
            videoProgressUpdate()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onDestroy() {
        NormalProgressDialog.stopLoading()
//        ConfigUtils.getInstance().magicFilterType = MagicFilterType.NONE
        animator?.cancel()
        mMediaPlayer.release()
//        mMp4Composer?.cancel()
        if (::mExtractVideoInfoUtil.isInitialized)
            mExtractVideoInfoUtil.release()
        mExtractFrameWorkThread?.stopExtract()
        _binding.videoThumbListview.removeOnScrollListener(mOnScrollListener)
        mUIHandler.removeCallbacksAndMessages(null)
        handler.removeCallbacksAndMessages(null)
        //Delete the preview image of each frame of the video
        if (!TextUtils.isEmpty(OutPutFileDirPath)) {
            VideoUtil.deleteFile(File(OutPutFileDirPath!!))
        }
        //remove cropped video, filter video
        val trimmedDirPath: String = VideoUtil.getTrimmedVideoDir(this, "Tex/trimmedVideo")
        if (!TextUtils.isEmpty(trimmedDirPath)) {
            VideoUtil.deleteFile(File(trimmedDirPath))
        }
        super.onDestroy()
    }

    companion object {
        private val TAG = TrimVideoActivity::class.java.simpleName
        private const val MIN_CUT_DURATION = 2 * 1000L // Minimum editing time 3s

        private const val MAX_CUT_DURATION = 60 * 1000L //How long can a video be cut at most?

        private const val MAX_COUNT_RANGE = 12 //How many pictures are there in the area of seekBar

        private val MARGIN: Int = UIUtils.dp2Px(62) //Spacing between left and right sides

        fun startActivity(
            context: Context,
            videoPath: Uri?,
            avidId: String,
            avidTakeId: Int = -1,
            isItFromGallery: Boolean
        ) {
            val intent = Intent(context, TrimVideoActivity::class.java)
            intent.putExtra("videoPath", videoPath)
            intent.putExtra("avidTakeId", avidTakeId)
            intent.putExtra("avidId", avidId)
            intent.putExtra("isFromGallery", isItFromGallery)
            context.startActivity(intent)
        }
    }
}