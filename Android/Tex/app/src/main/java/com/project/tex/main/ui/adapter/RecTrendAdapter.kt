package com.project.tex.main.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.tex.R
import com.project.tex.databinding.AvidTrendRecItemLayoutBinding


class RecTrendAdapter(val list: MutableList<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var currentPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AvidTrendHolder(
            AvidTrendRecItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ).apply {
            setIsRecyclable(false)
        }
    }

    public fun playAt(pos: Int) {
        if (currentPos == -1)
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

    override fun getItemCount(): Int = list.size

    inner class AvidTrendHolder(val view: AvidTrendRecItemLayoutBinding) :
        RecyclerView.ViewHolder(view.root), MediaPlayer.OnPreparedListener {
        fun init(toPlay: Boolean, position: Int) {
            view.videoView.apply {
                if (isPlaying) {
                    view.iv.animate().alpha(1f).withEndAction {
//                        pause()
                    }
                    return
                }
                val ss =
                    "android.resource://" + view.root.context.getPackageName() + "/" + list.get(
                        position
                    )
                setVideoURI(Uri.parse(ss))
                if (toPlay) {
                    start()
                } else {
                    pause()
                    view.iv.animate().alpha(1f).withEndAction {
//                        pause()
                    }
                }
                Glide.with(view.iv).load(list.get(position)).into(view.iv)
            }
            view.videoView.setOnPreparedListener(this@AvidTrendHolder)
            view.root.setOnClickListener {
                if (currentPos == absoluteAdapterPosition) return@setOnClickListener
                currentPos = absoluteAdapterPosition
                notifyDataSetChanged()
            }
        }

        override fun onPrepared(mp: MediaPlayer?) {
            mp!!.setOnInfoListener(MediaPlayer.OnInfoListener { mp, what, extra ->
                Log.d("mp", "onInfo, what = $what")
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // video started; hide the placeholder.
                    view.iv.animate().alpha(0f)
                    return@OnInfoListener true
                }
                false
            })
        }

    }

    companion object {
        var dra: Drawable? = null
    }
}