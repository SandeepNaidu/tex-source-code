package com.project.tex.main.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.danikula.videocache.CacheListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.storage.FirebaseStorage
import com.project.tex.GlobalApplication
import com.project.tex.R
import com.project.tex.base.FileDownloader
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.*
import com.project.tex.main.HomeActivity
import com.project.tex.main.model.AvidData
import com.project.tex.main.ui.player.MediaObserver
import com.project.tex.post.model.AllPostData
import com.project.tex.recruiter.ui.home.CenterLayoutManager
import com.project.tex.utils.IntentUtils
import com.project.tex.utils.TimeAgo2
import com.project.tex.utils.TimeLeft2
import com.project.tex.utils.ViewUtils.dpToPx
import com.project.tex.utils.pdf.PdfUtils
import com.volokh.danylo.hashtaghelper.HashTagHelper
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class PostAdapter(
    val actions: PostActions,
    val lifecycleOwner: LifecycleOwner,
    val list: MutableList<Any>,
    screenOffObserver: LiveData<Boolean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        screenOffObserver.observe(lifecycleOwner) {
            pauseTrendingAvids(it)
            if (it) {
                pauseAll()
            }
        }
    }

    private fun pauseTrendingAvids(isScreenOff: Boolean) {
        toPauseTrendingAvid = isScreenOff
    }

    private var trendLastPos: Int = 0
    private val TAG: String = PostAdapter::class.java.simpleName
    private var observer: MediaObserver? = null

    private var recyclerState: Parcelable? = null

    var positionToPlayVideo = 0
    var toPauseTrendingAvid = false
    var isTrendAvidVisible = false

    private val fileDownloader by lazy {
        FileDownloader(
            OkHttpClient.Builder().build()
        )
    }

    fun runMedia(mPlayer: MediaPlayer, progress: ProgressBar) {
        mPlayer.start()
        observer = MediaObserver(mPlayer, progress)
        Thread(observer).start()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_VIDEO -> {
                VideoHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), actions
                ).apply {
                    if (simplePlayer != null) {
                        simplePlayer?.stop()
                        simplePlayer?.release()
                        simplePlayer = null
                    }
                    simplePlayer = getPlayer(itemView.context)
                    videoUi.videoView.player = simplePlayer
                    simplePlayer?.addListener(this)
                }
            }
            TYPE_TRENDS -> {
                TrendsHolder(
                    TrendingAvidLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            TYPE_IMAGE -> {
                ImageHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), actions
                )
            }
            TYPE_SURVEY -> {
                SurveyHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), actions
                )
            }
            TYPE_EVENT -> {
                EventHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), actions
                )
            }
            TYPE_DOCUMENT -> {
                DocumentHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), actions
                )
            }
            TYPE_AUDIO -> {
                AudioHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), actions
                ).apply {

                }
            }
            else -> {
                throw java.lang.RuntimeException("No such view type! ViewType - ${viewType}")
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is VideoHolder) {
            holder.playVideo(holder.itemView.tag == positionToPlayVideo)
        } else if (holder is TrendsHolder) {
            holder.playVideos()
        } else if (holder is AudioHolder) {
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is VideoHolder) {
            holder.releasePlayer()
//            holder.setIsRecyclable(true);
        } else if (holder is DocumentHolder) {
            holder.close()
        } else if (holder is TrendsHolder) {
            holder.adapter?.pauseAllVideo()
        } else if (holder is AudioHolder) {
            if (holder.mediaPlayer.isPlaying) holder.mediaPlayer.pause()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.itemView.tag = position
        if (holder is VideoHolder) {
            holder.init(position == positionToPlayVideo, position)
            holder.bind(list[position] as? AllPostData.Body.Posts.Video)
        } else if (holder is ImageHolder) {
            holder.bind(list[position] as? AllPostData.Body.Posts.Image)
        } else if (holder is SurveyHolder) {
            holder.bind(list[position] as? AllPostData.Body.Posts.Poll)
        } else if (holder is EventHolder) {
            holder.bind(list[position] as? AllPostData.Body.Posts.Event)
        } else if (holder is TrendsHolder) {
            holder.start(list[position] as? List<AvidData>?, position == positionToPlayVideo)
        } else if (holder is AudioHolder) {
            holder.ui()
            holder.bind(list[position] as? AllPostData.Body.Posts.Music)
        } else if (holder is DocumentHolder) {
            holder.bind(list[position] as? AllPostData.Body.Posts.Document)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads[0] is List<*>) {
                (payloads[0] as? Collection<AvidData>)?.let {
                    (list[position] as? MutableList<AvidData>?)?.addAll(
                        it
                    )
                    (holder as? TrendsHolder)?.start(
                        list[position] as? List<AvidData>?, position == positionToPlayVideo
                    )
                }
            } else {
                (payloads[0] as? Any)?.let {
                    when (list.get(position)) {
                        is AllPostData.Body.Posts.Video -> {
                            if (it is AllPostData.Body.Posts.Video) {
                                list.set(position, it)
                                (holder as VideoHolder).bind(it)
                            }
                        }
                        is AllPostData.Body.Posts.Music -> {
                            if (it is AllPostData.Body.Posts.Music) {
                                list.set(position, it)
                                (holder as AudioHolder).bind(it)
                            }
                        }
                        is AllPostData.Body.Posts.Document -> {
                            if (it is AllPostData.Body.Posts.Document) {
                                list.set(position, it)
                                (holder as DocumentHolder).bind(it)
                            }
                        }
                        is AllPostData.Body.Posts.Image -> {
                            if (it is AllPostData.Body.Posts.Image) {
                                list.set(position, it)
                                (holder as ImageHolder).bind(it)
                            }
                        }
                        is AllPostData.Body.Posts.Event -> {
                            if (it is AllPostData.Body.Posts.Event) {
                                list.set(position, it)
                                (holder as EventHolder).bind(it)
                            }
                        }
                        is AllPostData.Body.Posts.Poll -> {
                            if (it is AllPostData.Body.Posts.Poll) {
                                list.set(position, it)
                                (holder as SurveyHolder).bind(it)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is AllPostData.Body.Posts.Image -> {
                TYPE_IMAGE
            }
            is AllPostData.Body.Posts.Music -> {
                TYPE_AUDIO
            }
            is AllPostData.Body.Posts.Document -> {
                TYPE_DOCUMENT
            }
            is AllPostData.Body.Posts.Video -> {
                TYPE_VIDEO
            }
            is AllPostData.Body.Posts.Event -> {
                TYPE_EVENT
            }
            is AllPostData.Body.Posts.Poll -> {
                TYPE_SURVEY
            }
            is List<*> -> {
                TYPE_TRENDS
            }
            else -> {
                -1
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateVisibleItem(visibleItemPosition: Int) {
        if (positionToPlayVideo == visibleItemPosition) return
//        notifyItemChanged(positionToPlayVideo)
        positionToPlayVideo = visibleItemPosition
        val type = getItemViewType(visibleItemPosition)
        if (type == TYPE_TRENDS || type == TYPE_SURVEY || type == TYPE_DOCUMENT || type == TYPE_EVENT || type == TYPE_IMAGE) {
            return
        }
    }

    fun pauseAll() {
//        if (positionToPlayVideo == -1) return
        positionToPlayVideo = -1
        notifyDataSetChanged()
    }

    fun playTrendingAvid(b: Boolean) {
        if (b) {
            Log.d(TAG, "playTrendingAvid: true")
            isTrendAvidVisible = true
            if (positionToPlayVideo == 1) return
            positionToPlayVideo = 1
        } else {
            Log.d(TAG, "playTrendingAvid: false")
            isTrendAvidVisible = false
            if (positionToPlayVideo == -1) return
            positionToPlayVideo = -1
        }
        notifyDataSetChanged()
    }

    private inner class VideoHolder(val view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action), Player.Listener, CacheListener {
        var simplePlayer: ExoPlayer? = null
        val videoUi: VideoPlayBinding

        init {
            val prm =
                (view as VideoPostItemLayoutBinding).postFrame.layoutParams as ViewGroup.LayoutParams
            prm.height = ViewGroup.LayoutParams.WRAP_CONTENT
            view.postFrame.layoutParams = prm

            val videoPlay = VideoPlayBinding.inflate(LayoutInflater.from(view.root.context))
            view.postFrame.addView(videoPlay.root)
            videoPlay.root.layoutParams.height = dpToPx(220)
            videoUi = videoPlay
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> {
                    videoUi.iv.isVisible = false
                    "ExoPlayer.STATE_READY     -"
                }
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d("STATE", "changed state to $stateString")
        }

        fun init(toPlay: Boolean, position: Int) {
            (list.get(position) as AllPostData.Body.Posts.Video).videoUrl?.let {
                if (toPlay) prepareMedia(it, itemView.context)
                else {
                    playVideo(false)
                }
            }

            videoUi.videoView.setOnClickListener {
                if (positionToPlayVideo == position) {
                    pauseVideo()
                    positionToPlayVideo = -1
                    return@setOnClickListener
                }
                positionToPlayVideo = position
                playAt(positionToPlayVideo)
                notifyDataSetChanged()
            }

//            simplePlayer?.playWhenReady = toPlay
            if (!toPlay) {
                videoUi.iv.animate().alpha(1f)
            } else {
                videoUi.iv.animate().alpha(0f)
            }

            Glide.with(videoUi.iv).asBitmap()
                .load((list.get(position) as AllPostData.Body.Posts.Video).thumbUrl)
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
//                        videoUi.root.layoutParams?.height = resource?.height
                        videoUi.iv.setImageBitmap(resource)
                        return false
                    }
                }).into(videoUi.iv)

        }

        fun playAt(pos: Int) {
            if (positionToPlayVideo == -1) positionToPlayVideo = pos
            notifyDataSetChanged()
        }

        fun playVideo(toPlay: Boolean) {
            simplePlayer?.playWhenReady = toPlay
        }

        private fun restartVideo(it: String, context: Context) {
            if (simplePlayer == null) {
                prepareMedia(it, context)
            } else {
                simplePlayer?.seekToDefaultPosition()
                simplePlayer?.playWhenReady = true
            }
        }

        private fun pauseVideo() {
            simplePlayer?.playWhenReady = false
            videoUi.iv.animate().alpha(1f)
        }

        private fun prepareVideoPlayer(context: Context) {
            simplePlayer = ExoPlayer.Builder(context).build()
        }

        fun getPlayer(context: Context): ExoPlayer? {
            if (simplePlayer == null) {
                prepareVideoPlayer(context)
            }
            return simplePlayer
        }

        private fun prepareMedia(linkUrl: String, context: Context) {
            Log.d("prepareMedia", "prepareMedia linkUrl: $linkUrl")

            val proxyUrl = GlobalApplication.instance.getProxy(context)!!.getProxyUrl(linkUrl)
            val uri = Uri.parse(proxyUrl)

            val mediaItem = MediaItem.fromUri(uri)
            val dataSourceFactory = DefaultDataSourceFactory(context, "tex_video")
            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

            simplePlayer?.setMediaSource(mediaSource)
            simplePlayer?.prepare()
            simplePlayer?.repeatMode = Player.REPEAT_MODE_OFF
            simplePlayer?.playWhenReady = false
            simplePlayer?.addListener(this)
        }

        fun releasePlayer() {
            simplePlayer?.stop()
            simplePlayer?.release()
        }

        override fun onCacheAvailable(cacheFile: File?, url: String?, percentsAvailable: Int) {
            videoUi.iv.animate().alpha(0f)
        }

        fun bind(data: AllPostData.Body.Posts.Video?) {
            (view as VideoPostItemLayoutBinding).let {
                if (data != null) {
                    view.usernameTv.text =
                        "${data.artistFirstName ?: ""} ${data.artistLastName ?: ""}"
                    data.address = updateLocation(data.latlong, data.address)
                    view.likeCtv.text = "${data.likeCount ?: "0"} likes"
                    view.shareCtv.text = "${data.shareCount ?: "0"} Shares"
                    view.saveCtv.text = "${data.saveCount ?: "0"} saves"
                    view.descriptionTv.originalText = (data.caption ?: "")

                    view.likeCtv.isChecked = data.isLiked == 1
                    view.shareCtv.isChecked = data.isShared == 1
                    view.saveCtv.isChecked = data.isSaved == 1
                    view.postTimeTv.text = TimeAgo2().covertTimeToText(data.createAt)
                    Glide.with(itemView).load(data.artistProfileImage)
                        .error(R.drawable.default_user).into(view.profileImg)
                }
            }
        }
    }

    private inner class DocumentHolder(val view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action), PdfUtils.PDFListener {
        private var pdfUtils: PdfUtils? = null
        val surveyContent: FragmentPdfRendererABinding
        private val TAG: String = "DocumentHolder"

        init {
            view.root.context.apply {
//                ((view as VideoPostItemLayoutBinding).postFrame.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
//                    "34:35"
                ((view as VideoPostItemLayoutBinding).postFrame.layoutParams as ConstraintLayout.LayoutParams).height =
                    this.dpToPx(280)
                surveyContent =
                    FragmentPdfRendererABinding.inflate(LayoutInflater.from(view.root.context))
                view.postFrame.addView(surveyContent.root)
            }
        }

        fun bind(data: AllPostData.Body.Posts.Document?) {
            Glide.with(itemView).asBitmap().load(data?.thumbUrl).into(surveyContent.preview)
            data?.documentUrl?.let {
                val file = File(itemView.context.cacheDir.path + File.separator + "Document")

                val filename = try {
                    FirebaseStorage.getInstance()
                        .getReferenceFromUrl(data.documentUrl).name + ".pdf"
                } catch (e: Exception) {
                    ""
                }
                val docFile = File(file.path + File.separator + filename)
                if (filename.isNotEmpty() && !docFile.exists()) {
                    if (!(docFile.parentFile?.exists() == true)) {
                        docFile.parentFile?.mkdirs()
                    }
                    docFile.deleteRecursively()
                    try {
                        docFile.createNewFile()
                        fileDownloader.download(it, docFile).throttleFirst(2, TimeUnit.SECONDS)
                            .toFlowable(BackpressureStrategy.LATEST).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe({
//                            fragment.msg.showShortMsg("$it% Downloaded")
                            }, {
                                (itemView.context as? BaseActivity<*, *>)?.msg?.showShortMsg(it.localizedMessage)
                            }, {
                                Log.d(TAG, "bind: Complete Downloaded")
//                                fragment.msg.showShortMsg("Complete Downloaded")
                                surveyContent.pdfView.fromFile(docFile)
                                    .enableSwipe(true) // allows to block changing pages using swipe
                                    .swipeHorizontal(false).enableDoubletap(false).pageSnap(true)
                                    .disableLongpress()
                                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                                    .spacing(16)
                                    .pageFitPolicy(FitPolicy.HEIGHT) // mode to fit pages in the view
                                    .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
                                    .load()
                                surveyContent.preview.isVisible = false
                            })
                    } catch (e: Exception) {
                        Log.e(TAG, "bind: ", e)
                    }
                } else {
                    surveyContent.pdfView.fromFile(docFile)
                        .enableSwipe(true) // allows to block changing pages using swipe
                        .swipeHorizontal(true).pageSnap(true).enableDoubletap(false)
                        .disableLongpress()
                        .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                        .spacing(16)
                        .pageFitPolicy(FitPolicy.HEIGHT) // mode to fit pages in the view
                        .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
                        .load()
                    surveyContent.preview.isVisible = false
                }
            }
            (view as VideoPostItemLayoutBinding).let {
                if (data != null) {
                    view.usernameTv.text =
                        "${data.artistFirstName ?: ""} ${data.artistLastName ?: ""}"
                    data.address = updateLocation(data.latlong, data.address)
                    view.likeCtv.text = "${data.likeCount ?: "0"} likes"
                    view.shareCtv.text = "${data.shareCount ?: "0"} Shares"
                    view.saveCtv.text = "${data.saveCount ?: "0"} saves"
                    view.descriptionTv.originalText = (data.caption ?: "")

                    view.likeCtv.isChecked = data.isLiked == 1
                    view.shareCtv.isChecked = data.isShared == 1
                    view.saveCtv.isChecked = data.isSaved == 1
                    view.postTimeTv.text = TimeAgo2().covertTimeToText(data.createAt)
                    Glide.with(itemView).load(data.artistProfileImage)
                        .error(R.drawable.default_user).into(view.profileImg)
                }
            }
        }

        fun close() {
//            pdfUtils?.closeRenderer()
        }

        override fun loaded() {
            //pdf loaded
        }

        override fun fail(e: Throwable) {
            //pdf load failed
        }

        override fun complete() {
            //pdf load complete
            if ((view as VideoPostItemLayoutBinding).postFrame.childCount > 0) return
            pdfUtils?.renderAllPages(object : PdfUtils.PageReadyListener {
                override fun onPagesReady(list: List<Bitmap>) {
                    Log.d(TAG, "onPagesReady: called with items count : " + list.size)
                }

                override fun fail(e: Throwable) {
                    Log.e(TAG, "fail: ", e)
                }

                override fun complete() {
                    Log.d(TAG, "complete: called")
                    if (view.postFrame.childCount > 0) return
                }
            })
        }
    }

    private inner class AudioHolder(val view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action) {
        private var audioUi: AudioContentLayoutBinding? = null
        var seekPosition = 0
        var mediaPlayer: MediaPlayer = MediaPlayer()
        private var url: String? = ""

        private fun playMedia(url: String) {
            try {
                audioUi?.playerControl?.isVisible = false
                audioUi?.progress?.isVisible = true
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    audioUi?.playerWaves?.playAnimation()
                    audioUi?.playerControl?.isChecked = !(audioUi?.playerControl?.isChecked ?: true)
                    audioUi?.progressBar?.max = mediaPlayer.duration
                    it.seekTo(seekPosition)
                    audioUi?.progressBar?.let { it1 -> runMedia(mediaPlayer, it1) }
                    audioUi?.playerControl?.isVisible = true
                    audioUi?.progress?.isVisible = false
                }
            } catch (e: Exception) {
                audioUi?.playerWaves?.pauseAnimation()
                audioUi?.playerControl?.isVisible = true
                audioUi?.progress?.isVisible = false
                Log.e("AudioPost ERROR", "playMedia: ", e)
            }
        }

        private fun pauseMedia() {

        }

        fun ui() {
            if ((view as VideoPostItemLayoutBinding).postFrame.childCount == 0) {
                audioUi = AudioContentLayoutBinding.inflate(LayoutInflater.from(view.root.context))
                view.postFrame.addView(audioUi!!.root)
                audioUi?.apply {
                    progressBar.progressDrawable.mutate()
                    progressBar.progress = seekPosition
                    progressBar.max = mediaPlayer.duration
                    playerControl.setOnClickListener {
                        url?.let { it1 ->
                            if (!playerControl.isChecked) {
                                playMedia(it1)
                            } else {
                                playerControl.isChecked = false
                                playerWaves.pauseAnimation()
                                playerWaves.frame = 0
                                mediaPlayer.pause()
                                observer?.stop()
                                seekPosition = mediaPlayer.currentPosition
                                mediaPlayer.reset()
                            }
                            mediaPlayer.setOnCompletionListener {
                                observer?.stop()
                                audioUi?.playerControl?.isVisible = true
                                audioUi?.progress?.isVisible = false
                                playerWaves.pauseAnimation()
                                playerWaves.frame = 0
                                playerControl.isChecked = false
                                progressBar.progress = it.currentPosition
                            }
                        } ?: kotlin.run {
                            audioUi?.playerControl?.isVisible = true
                            audioUi?.progress?.isVisible = false
                            Toast.makeText(
                                view.root.context, "Failed to play audio.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        fun bind(data: AllPostData.Body.Posts.Music?) {
            view.root.findViewById<ProgressBar>(R.id.progressBar).apply {
                progress = mediaPlayer.currentPosition
            }
            if (data != null) {
                url = data.audioUrl
            }
            audioUi?.apply {
                if (mediaPlayer.isPlaying) {
                    playerControl.isChecked = true
                    if (playerControl.isChecked) {
                        playerWaves.playAnimation()
                        runMedia(mediaPlayer, progressBar)
                    }
                } else {
                    playerControl.isChecked = false
                    playerWaves.pauseAnimation()
                    playerWaves.frame = 0
                    seekPosition = mediaPlayer.currentPosition
                    observer?.stop()
                }
                if (data != null) {
                    Glide.with(itemView.context).load(data.thumbUrl)
                        .placeholder(R.drawable.ic_album_placeholder)
                        .error(R.drawable.ic_album_placeholder).into(audioThumbnailCv)
                }
            }
            if (data != null) {
                (view as VideoPostItemLayoutBinding).usernameTv.text =
                    "${data.artistFirstName ?: ""} ${data.artistLastName ?: ""}"
                data.address = updateLocation(data.latlong, data.address)
                view.likeCtv.text = "${data.likeCount ?: "0"} likes"
                view.shareCtv.text = "${data.shareCount ?: "0"} Shares"
                view.saveCtv.text = "${data.saveCount ?: "0"} saves"

                view.descriptionTv.originalText = (data.caption ?: "")

                view.likeCtv.isChecked = data.isLiked == 1
                view.shareCtv.isChecked = data.isShared == 1
                view.saveCtv.isChecked = data.isSaved == 1
                view.postTimeTv.text = TimeAgo2().covertTimeToText(data.createAt)
                Glide.with(itemView).load(data.artistProfileImage).error(R.drawable.default_user)
                    .into(view.profileImg)
            }
        }
    }

    private inner class ImageHolder(val view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action) {
        fun bind(data: AllPostData.Body.Posts.Image?) {
            val prm =
                (view as VideoPostItemLayoutBinding).postFrame.layoutParams as ViewGroup.LayoutParams
            prm.height = dpToPx(view.root.context, 192)
            view.postFrame.layoutParams = prm
            val img = ImageView(view.root.context)
            view.postFrame.addView(img)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            if (data != null) {
                val thumbnail: RequestBuilder<Bitmap> =
                    Glide.with(itemView).asBitmap().load(data.thumbUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(img).asBitmap().load(data.imageUrl).thumbnail(thumbnail)
                    .error(data.thumbUrl).error(R.drawable.ic_no_preview).into(img)
            }
            view.let {
                if (data != null) {
                    view.usernameTv.text =
                        "${data.artistFirstName ?: ""} ${data.artistLastName ?: ""}"
                    data.address = updateLocation(data.latlong, data.address)
                    view.likeCtv.text = "${data.likeCount ?: "0"} likes"
                    view.shareCtv.text = "${data.shareCount ?: "0"} Shares"
                    view.saveCtv.text = "${data.saveCount ?: "0"} saves"
                    view.descriptionTv.originalText = (data.caption ?: "")

                    view.likeCtv.isChecked = data.isLiked == 1
                    view.shareCtv.isChecked = data.isShared == 1
                    view.saveCtv.isChecked = data.isSaved == 1
                    view.postTimeTv.text = TimeAgo2().covertTimeToText(data.createAt)
                    Glide.with(itemView).load(data.artistProfileImage)
                        .error(R.drawable.default_user).into(view.profileImg)
                }
            }
        }
    }

    private inner class SurveyHolder(val view: ViewBinding, val action: PostActions) :
        BasePostHolder(view.root, action) {
        fun bind(data: AllPostData.Body.Posts.Poll?) {
            (view as VideoPostItemLayoutBinding).let {
                if (data != null) {
                    view.usernameTv.text =
                        "${data.artistFirstName ?: ""} ${data.artistLastName ?: ""}"
                    data.address = updateLocation(data.latlong, data.address)
                    view.likeCtv.text = "${data.likeCount ?: "0"} likes"
                    view.shareCtv.text = "${data.shareCount ?: "0"} Shares"
                    view.saveCtv.text = "${data.saveCount ?: "0"} saves"
                    view.descriptionTv.isVisible = false

                    view.likeCtv.isChecked = data.isLiked == 1
                    view.shareCtv.isChecked = data.isShared == 1
                    view.saveCtv.isChecked = data.isSaved == 1
                    view.postTimeTv.text = TimeAgo2().covertTimeToText(data.createAt)
                    Glide.with(itemView).load(data.artistProfileImage)
                        .error(R.drawable.default_user).into(view.profileImg)
                    val surveyContent =
                        SurveyContentLayoutBinding.inflate(LayoutInflater.from(view.root.context))
                    surveyContent.questionTv.text = data.question
                    val options = data.options
                    if (options != null) {
                        var total = 0
                        options.forEach {
                            total += it?.count ?: 0
                        }
                        surveyContent.postVotesTv.text =
                            itemView.context.resources.getQuantityString(
                                R.plurals.vote, total, total
                            )
                        surveyContent.optionsLl.removeAllViews()
                        val pollTimeLeft = TimeLeft2.getLefttime(data.createAt, data.duration)
                        for (i in options) {
                            val questionItem =
                                QuestOptionsItemLayoutBinding.inflate(LayoutInflater.from(view.root.context))

                            questionItem.optionText.text = i?.optionText
                            surveyContent.optionsLl.addView(questionItem.root)
                            val params = questionItem.root.layoutParams as MarginLayoutParams
                            params.height = dpToPx(view.root.context, 32)
                            params.setMargins(0, dpToPx(view.root.context, 16), 0, 0)
                            questionItem.root.layoutParams = params
                            if (data.isVoted == 1 || pollTimeLeft < 3000) {
                                questionItem.percent.isVisible = true
                                questionItem.progress.isVisible = true
                                val percent = if (total == 0) 0 else (((i?.count
                                    ?: 0).toFloat() / total) * 100).roundToInt()
                                questionItem.progress.progress = percent
                                questionItem.percent.text = "${percent}%"
                                questionItem.optionText.gravity = Gravity.START
                            } else {
                                questionItem.optionText.gravity = Gravity.CENTER
                                questionItem.root.setOnClickListener {
                                    i?.id?.let { it1 ->
                                        if (TimeLeft2.getLefttime(
                                                data.createAt, data.duration
                                            ) > 3000
                                        ) {
                                            if (bindingAdapterPosition == NO_POSITION) return@setOnClickListener
                                            action.onVoted(
                                                bindingAdapterPosition, data, it1
                                            )
                                        } else {
                                            (itemView.context as? HomeActivity)?.msg?.showShortMsg("Polling time over!")
                                            surveyContent.postTimeLeftTv.text =
                                                TimeLeft2.covertTimeToText(
                                                    data.createAt, data.duration
                                                )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    surveyContent.postTimeLeftTv.text =
                        TimeLeft2.covertTimeToText(data.createAt, data.duration)

                    view.postFrame.removeAllViews()
                    view.postFrame.addView(surveyContent.root)
                    val prm = view.postFrame.layoutParams as ViewGroup.LayoutParams
                    prm.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    view.postFrame.layoutParams = prm
                    view.postFrame.setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

    private inner class EventHolder(val view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action) {
        private var eventUi =
            EventContentItemLayoutBinding.inflate(LayoutInflater.from(view.root.context))

        init {
            (view as VideoPostItemLayoutBinding).postFrame.addView(eventUi.root)
        }

        fun bind(data: AllPostData.Body.Posts.Event?) {
            Glide.with(itemView.context).load(data?.eventImageUrl).into(eventUi.img)
            eventUi.btnTv.setOnClickListener {
                if (data != null) {
                    IntentUtils.openWebPage(itemView.context, data.eventExternalLink)
                }
            }

            (view as VideoPostItemLayoutBinding).let {
                if (data != null) {
                    view.usernameTv.text =
                        "${data.artistFirstName ?: ""} ${data.artistLastName ?: ""}"
                    view.likeCtv.text = "${data.likeCount ?: "0"} likes"
                    view.shareCtv.text = "${data.shareCount ?: "0"} Shares"
                    view.saveCtv.text = "${data.saveCount ?: "0"} saves"
                    view.descriptionTv.originalText = (data.description ?: "")

                    view.likeCtv.isChecked = data.isLiked == 1
                    view.shareCtv.isChecked = data.isShared == 1
                    view.saveCtv.isChecked = data.isSaved == 1

                    view.postTimeTv.text = TimeAgo2().covertTimeToText(data.createAt)
                    Glide.with(itemView).load(data.artistProfileImage)
                        .error(R.drawable.default_user).into(view.profileImg)
                    data.address = updateLocation(data.latlong, data.address)
                }
            }
        }

    }

    private inner class TrendsHolder(val view: ViewBinding) : RecyclerView.ViewHolder(view.root) {
        var adapter: TrendAdapter? = null

        fun start(data: List<AvidData>?, b: Boolean) {
            if (data == null || data.isEmpty()) {
                view.root.isVisible = false
                return
            }
            if (toPauseTrendingAvid) {
                adapter?.pauseAllVideo()
            }
            view.root.isVisible = true
            (view as TrendingAvidLayoutBinding).trendingAvidRv.layoutManager?.onRestoreInstanceState(
                recyclerState
            )
            if (adapter == null) {
                adapter = TrendAdapter(data)
            }
            view.trendingAvidRv.layoutManager =
                CenterLayoutManager(view.root.context, LinearLayoutManager.HORIZONTAL, false)
            view.trendingAvidRv.adapter = adapter
            adapter!!.setListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    val stateString: String = when (playbackState) {
                        ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                        ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                        ExoPlayer.STATE_READY -> {
                            "ExoPlayer.STATE_READY     -"
                        }
                        ExoPlayer.STATE_ENDED -> {
//                            trendLastPos = adapter!!.playNext()
                            view.trendingAvidRv.smoothScrollToPosition(++trendLastPos)
                            "ExoPlayer.STATE_ENDED     -"
                        }
                        else -> "UNKNOWN_STATE             -"
                    }
                    Log.d("STATE", "changed state to $stateString")
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
//                    trendLastPos = adapter!!.playNext()
                    view.trendingAvidRv.smoothScrollToPosition(++trendLastPos)
                }
            })
            view.trendingAvidRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val visibleItem =
                            (view.trendingAvidRv.layoutManager as CenterLayoutManager).findFirstCompletelyVisibleItemPosition()
                        if (visibleItem != NO_POSITION) {
                            trendLastPos = visibleItem
                        }
                        adapter?.playAt(trendLastPos)
                    }
                }
            })
            (view.trendingAvidRv.adapter as TrendAdapter).apply {
                if (isTrendAvidVisible) {
                    playAt(trendLastPos)
                    view.trendingAvidRv.smoothScrollToPosition(trendLastPos)
                } else {
                    playPause(b)
                }
            }

        }

        fun playVideos() {

        }

        init {
            (view as TrendingAvidLayoutBinding).apply {
                trendingAvidRv.recycledViewPool.setMaxRecycledViews(0, 0)
                trendingAvidRv.addOnScrollListener(object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        recyclerState = recyclerView.layoutManager!!.onSaveInstanceState()
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                        }
                    }
                })
            }
        }
    }

    private open inner class BasePostHolder(view: View, action: PostActions) :
        RecyclerView.ViewHolder(view) {
        val userPicImage = view.findViewById<CircleImageView>(R.id.profile_img)
        val usernameText = view.findViewById<AppCompatTextView>(R.id.username_tv)
        val postLocationText = view.findViewById<AppCompatTextView>(R.id.post_location_tv)
        val postTimeText = view.findViewById<AppCompatTextView>(R.id.post_time_tv)
        val likes = view.findViewById<CheckedTextView>(R.id.like_ctv)
        val shares = view.findViewById<CheckedTextView>(R.id.share_ctv)
        val saves = view.findViewById<CheckedTextView>(R.id.save_ctv)
        val postFrame = view.findViewById<FrameLayout>(R.id.post_frame)
        val optionsMenu = view.findViewById<ImageView>(R.id.options_iv)
        val description = view.findViewById<AppCompatTextView>(R.id.description_tv)
        private var mEditTextHashTagHelper: HashTagHelper = HashTagHelper.Creator.create(
            ContextCompat.getColor(itemView.context, R.color.text_main), null
        )

        init {
            optionsMenu.setOnClickListener {
                if (bindingAdapterPosition == NO_POSITION) return@setOnClickListener
                action.onOptionMenuClick(bindingAdapterPosition, list[bindingAdapterPosition])
            }
            mEditTextHashTagHelper.handle(description)

            userPicImage.setOnClickListener {
                if (bindingAdapterPosition == NO_POSITION) return@setOnClickListener
                action.onProfileIconClicked(list[bindingAdapterPosition])
            }

            usernameText.setOnClickListener {
                if (bindingAdapterPosition == NO_POSITION) return@setOnClickListener
                action.onProfileIconClicked(list[bindingAdapterPosition])
            }

            shares.setOnClickListener {
                if (bindingAdapterPosition == NO_POSITION) return@setOnClickListener
                action.onShareClick(bindingAdapterPosition, list[bindingAdapterPosition])
            }

            saves.setOnClickListener {
                if (bindingAdapterPosition == NO_POSITION) return@setOnClickListener
                action.onSaveClick(bindingAdapterPosition, list[bindingAdapterPosition])
            }

            likes.setOnClickListener {
                if (bindingAdapterPosition == NO_POSITION) return@setOnClickListener
                action.onLikeClick(bindingAdapterPosition, list[bindingAdapterPosition])
            }
        }

        fun fetchLocation(location: Location, context: Context): LiveData<String> {
            val dataObserver = MutableLiveData<String>()
            Observable.just(location).flatMap {
                return@flatMap Observable.create<String> { emitter ->
                    val geocoder = Geocoder(context, Locale.ENGLISH)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(location.latitude,
                            location.longitude,
                            1,
                            Geocoder.GeocodeListener { addresses ->
                                getAddressResult(emitter, addresses)
                            })
                    } else {
                        val addresses = geocoder.getFromLocation(
                            location.latitude, location.longitude, 1
                        )
                        if (addresses != null) {
                            getAddressResult(emitter, addresses)
                        }
                    }
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.d(TAG, "getCurrentLocation: $it")
                dataObserver.value = it
            }, {
                Log.e(TAG, "getCurrentLocation: ", it)
            })
            return dataObserver
        }


        private fun getAddressResult(
            emitter: ObservableEmitter<String>, addresses: MutableList<Address>?
        ) {
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses.get(0)
                emitter.onNext(
                    ("${address.subAdminArea ?: ""}, ${address.adminArea ?: ""}").removePrefix(
                        ","
                    ).removeSuffix(",").removePrefix(",")
                )
                emitter.onComplete()
            } else {
                emitter.onNext("~")
                emitter.onComplete()
            }
        }

        fun updateLocation(latlong: String?, address: String?): String {
            if (StringUtils.isEmpty(address)) {
                val latlngArray: List<String> = latlong?.split(",") ?: run {
                    postLocationText.text = "~"
                    return postLocationText.text.toString()
                }
                if (latlngArray.size > 1) {
                    fetchLocation(Location("api").apply {
                        latitude = latlngArray[0].toDoubleOrNull() ?: 0.0
                        longitude = latlngArray[1].toDoubleOrNull() ?: 0.0
                    }, itemView.context).observe(lifecycleOwner, androidx.lifecycle.Observer {
                        postLocationText.text = it
                    })
                } else {
                    postLocationText.text = "~"
                }
            } else {
                postLocationText.text = address ?: "~"
            }
            return postLocationText.text.toString()
        }
    }

    interface PostActions {
        fun onProfileIconClicked(postOwnerId: Any)
        fun onOptionMenuClick(pos: Int, postInfo: Any? = null)
        fun onSaveClick(pos: Int, postInfo: Any)
        fun onShareClick(pos: Int, postInfo: Any)
        fun onReportClick(pos: Int, postInfo: Any)
        fun onLikeClick(pos: Int, postInfo: Any)
        fun onVoted(pos: Int, postInfo: AllPostData.Body.Posts.Poll, clickedOptionId: Int)
    }

    companion object {
        const val TYPE_VIDEO = 1
        const val TYPE_TRENDS = 2
        const val TYPE_IMAGE = 3
        const val TYPE_EVENT = 4
        const val TYPE_SURVEY = 5
        const val TYPE_DOCUMENT = 6
        const val TYPE_AUDIO = 7
    }
}