package com.project.tex.main.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.project.tex.databinding.AvidTrendItemLayoutBinding
import com.project.tex.databinding.PagePdfRendererBinding


class ImageAdapter(val list: List<Bitmap>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DocImageHolder(
            PagePdfRendererBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (holder is DocImageHolder) {
            holder.itemView.tag = position
            holder.init(position)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class DocImageHolder(val view: PagePdfRendererBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun init(position: Int) {
            Glide.with(view.images).load(list.get(position)).into(view.images)
        }
    }

}