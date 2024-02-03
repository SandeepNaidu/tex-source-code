package com.project.tex.aritist_profile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.project.tex.R
import com.project.tex.aritist_profile.model.PortfolioListResponse
import com.project.tex.databinding.PageThumbItemBinding
import com.project.tex.main.model.AvidData
import com.project.tex.main.ui.adapter.PostAdapter
import com.project.tex.post.model.AllPostData
import com.project.tex.utils.ViewUtils


class PostGridAdapter(var list: List<Any>) :
    RecyclerView.Adapter<PostGridAdapter.PostThumbHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostThumbHolder {
        return PostThumbHolder(
            PageThumbItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(filterList: MutableList<Any>) {
        list = filterList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PostThumbHolder, position: Int) {
        holder.itemView.tag = position
        val d = list[position]
        val imageThumb =
            when (d) {
                is AllPostData.Body.Posts.Image -> {
                    ThumbItem(d.imageUrl, d.thumbUrl, d.id)
                }
                is AllPostData.Body.Posts.Document -> {
                    ThumbItem(null, d.thumbUrl, d.id)
                }
                is AllPostData.Body.Posts.Video -> {
                    ThumbItem(null, d.thumbUrl, d.id)
                }
                is AllPostData.Body.Posts.Event -> {
                    ThumbItem(d.eventImageUrl, null, d.id)
                }
                is AvidData -> {
                    ThumbItem(null, d.coverContent, d.avidId)
                }
                is PortfolioListResponse.Body.Portfolios.Data -> {
                    ThumbItem(d.thumbUrl, d.thumbUrl, d.id)
                }
                else -> {
                    ThumbItem(null, null, null)
                }
            }
        holder.init(position, imageThumb)
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is AllPostData.Body.Posts.Image -> {
                PostAdapter.TYPE_IMAGE
            }
            is AllPostData.Body.Posts.Music -> {
                PostAdapter.TYPE_AUDIO
            }
            is AllPostData.Body.Posts.Document -> {
                PostAdapter.TYPE_DOCUMENT
            }
            is AllPostData.Body.Posts.Video -> {
                PostAdapter.TYPE_VIDEO
            }
            is AllPostData.Body.Posts.Event -> {
                PostAdapter.TYPE_EVENT
            }
//            is List<*> -> {
//                PostAdapter.TYPE_TRENDS
//            }
            else -> {
                -1
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class PostThumbHolder(val view: PageThumbItemBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun init(position: Int, imageThumb: ThumbItem) {
            if (imageThumb.imageUrl != null) {
                Glide.with(view.imagePost).load(imageThumb.imageUrl)
                    .error(R.drawable.ic_no_preview)
                    .into(view.imagePost)
            } else {
                Glide.with(view.imagePost).load(imageThumb.thumbUrl)
                    .placeholder(R.drawable.ic_no_preview)
                    .error(R.drawable.ic_no_preview)
                    .into(view.imagePost)
            }
        }
    }

    data class ThumbItem(val imageUrl: String?, val thumbUrl: String?, val id: Int?)

}