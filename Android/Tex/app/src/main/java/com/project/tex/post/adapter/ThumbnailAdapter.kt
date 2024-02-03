package com.project.tex.post.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.tex.databinding.ThumbnailMediaItemBinding
import com.project.tex.db.table.LocalPost
import com.project.tex.main.ui.home.PostTypes
import com.project.tex.utils.ViewUtils

class ThumbnailAdapter(val list: MutableList<LocalPost>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEIGHT = ViewUtils.dpToPx(100)
    private val HEIGHT_DOC = ViewUtils.dpToPx(160)

    private var listener: ClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ThumbnailVH(
            ThumbnailMediaItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ThumbnailVH) {
            holder.bind(list[position])
            holder.view.root.setOnClickListener {
                listener?.onClick(list.get(position))
            }
            holder.view.iv.layoutParams.apply {
                if (list[position].postType == PostTypes.TYPE_DOCUMENT) {
                    height = HEIGHT_DOC
                } else {
                    height = HEIGHT
                }
            }
        }
    }

    public fun setListener(clickListener: ClickListener) {
        this.listener = clickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newlist: List<LocalPost>) {
        list.clear()
        list.addAll(newlist)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    inner class ThumbnailVH(val view: ThumbnailMediaItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(s: LocalPost) {
            Glide.with(view.root.context).load(s.postThumb).into(view.iv)
        }
    }

    fun getOptionData(): MutableList<LocalPost> {
        return list
    }

    interface ClickListener {
        fun onClick(post: LocalPost)
    }

}