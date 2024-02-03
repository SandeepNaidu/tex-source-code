package com.project.tex.main.ui.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danikula.videocache.CacheListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.project.tex.GlobalApplication
import com.project.tex.databinding.AvidTrendItemLayoutBinding
import com.project.tex.main.model.AvidData
import java.io.File


class TrendAdapter(val list: List<AvidData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    Player.Listener {

    private var plListener: Player.Listener? = null
    private var currentPos: Int = -1
    private val playerList: MutableList<ExoPlayer> = mutableListOf()
    private val playerSeekPosition: HashMap<Int, Long> = HashMap<Int, Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AvidTrendHolder(
            AvidTrendItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ).apply {
            if (simplePlayer != null) {
                playerList.remove(simplePlayer)
                simplePlayer?.stop()
                simplePlayer?.release()
                simplePlayer = null
            }
            simplePlayer = getPlayer(itemView.context)
            view.videoView.player = simplePlayer
            simplePlayer?.let {
                playerList.add(it)
                plListener?.let { it1 -> it.addListener(it1) }
            }
            setIsRecyclable(true)
            playerSeekPosition.put(bindingAdapterPosition, 0)
        }
    }

    fun setListener(listener: Player.Listener?) {
        plListener = listener
    }

    fun playPause(b: Boolean) {
        if (currentPos == -1) return
        if (b) playAt(currentPos)
        else pauseAllVideo()
    }

    fun pauseAllVideo() {
        playerList.forEach {
            it.pause()
        }
    }

    fun playAt(pos: Int) {
        if(currentPos == pos) return
        currentPos = pos
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (holder is AvidTrendHolder) {
            holder.itemView.tag = position
            holder.init(currentPos == position, position)
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as? AvidTrendHolder)?.let {
            it.playVideo(it.itemView.tag == currentPos)
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as? AvidTrendHolder)?.releasePlayer()
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int = list.size

    fun playNext(): Int {
        currentPos++
        playPause(true)
        return currentPos
    }

    inner class AvidTrendHolder(val view: AvidTrendItemLayoutBinding) :
        RecyclerView.ViewHolder(view.root), CacheListener {
        internal var simplePlayer: ExoPlayer? = null

        fun init(toPlay: Boolean, position: Int) {
            list.get(position).content?.let {
                if (toPlay) prepareMedia(it, itemView.context)
            }

            view.videoView.setOnClickListener {
                if (currentPos == position) {
                    pauseVideo()
                    currentPos = -1
                    return@setOnClickListener
                }
                currentPos = position
                playAt(currentPos)
                notifyDataSetChanged()
            }

//            simplePlayer?.playWhenReady = toPlay
            if (!toPlay) {
                view.iv.animate().alpha(1f)
            } else {
                view.iv.animate().alpha(0f)
            }

            Glide.with(view.iv).load(list.get(position).coverContent).into(view.iv)

        }

        public fun playVideo(toPlay: Boolean) {
            simplePlayer?.playWhenReady = toPlay
            simplePlayer?.repeatMode = Player.REPEAT_MODE_OFF
            if (bindingAdapterPosition == RecyclerView.NO_POSITION) return
            playerSeekPosition[bindingAdapterPosition]?.let { simplePlayer?.seekTo(it) }
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
            view.iv.animate().alpha(1f)
            simplePlayer?.currentPosition?.let {
                if (bindingAdapterPosition == RecyclerView.NO_POSITION) return
                playerSeekPosition.put(bindingAdapterPosition, it)
            }
        }

        private fun prepareVideoPlayer(context: Context) {
            simplePlayer = ExoPlayer.Builder(context).build()
        }

        internal fun getPlayer(context: Context): ExoPlayer? {
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
            simplePlayer?.repeatMode = Player.REPEAT_MODE_ONE
            simplePlayer?.playWhenReady = false
        }

        public fun releasePlayer() {
            simplePlayer?.stop()
            simplePlayer?.release()
        }

        override fun onCacheAvailable(cacheFile: File?, url: String?, percentsAvailable: Int) {
            view.iv.animate().alpha(0f)
        }
    }

}