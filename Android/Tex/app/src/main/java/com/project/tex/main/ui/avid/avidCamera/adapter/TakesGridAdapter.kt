package com.project.tex.main.ui.avid.avidCamera.adapter

import com.project.tex.db.table.AvidTakes
import androidx.recyclerview.widget.RecyclerView
import com.project.tex.main.ui.avid.avidCamera.adapter.TakesGridAdapter.TakesPreviewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import com.project.tex.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.project.tex.main.ui.avid.avidCamera.utils.VideoUtil
import com.project.tex.main.ui.avid.avidCamera.utils.UIUtils
import com.google.android.material.card.MaterialCardView

class TakesGridAdapter(private val mContext: Context, private var mDatas: List<AvidTakes>?) :
    RecyclerView.Adapter<TakesPreviewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var currentSelected = -1
    fun setData(datas: List<AvidTakes>?) {
        mDatas = datas
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mDatas == null) 0 else mDatas!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TakesPreviewHolder {
        return TakesPreviewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.gallery_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: TakesPreviewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val model = mDatas!![position]
        Glide.with(mContext)
            .load(VideoUtil.getVideoFilePath(model.videoThumb))
            .into(holder.mIv)
        if (currentSelected == position) {
            holder.mCard.strokeWidth = UIUtils.dp2Px(2)
            holder.mCard.cardElevation = UIUtils.dp2Px(8).toFloat()
        } else {
            holder.mCard.strokeWidth = UIUtils.dp2Px(0)
            holder.mCard.cardElevation = UIUtils.dp2Px(0).toFloat()
        }
        //
        holder.mCard.setOnClickListener { v: View? ->
            currentSelected = position
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(position, model)
            }
            notifyDataSetChanged()
        }
    }

    fun resetSelection() {
        currentSelected = -1
    }

    class TakesPreviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIv: ImageView
        var mCard: MaterialCardView

        init {
            mIv = itemView.findViewById(R.id.gallery_preview)
            mCard = itemView.findViewById(R.id.avid_root)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, model: AvidTakes?)
    }
}