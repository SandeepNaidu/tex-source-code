package com.project.tex.settings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.tex.R
import com.project.tex.databinding.CategoryItemLayoutBinding
import com.project.tex.databinding.ItemSettingListBinding
import com.project.tex.settings.viewmodel.SettingItem

class SettingAdapter(val list: List<SettingItem>, val click: SettingAdapter.OnClickListener) :
    RecyclerView.Adapter<SettingAdapter.SettingViewHolder>() {
    var selectedPos: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        return SettingViewHolder(
            ItemSettingListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ).root
        )
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.text1).apply {
            text = list.get(position).title
            setOnClickListener {
                selectedPos = position
                click.itemClicked(selectedPos, list[position].itemId)
            }
            setCompoundDrawablesWithIntrinsicBounds(
                list[position].startIcon,
                0,
                list[position].endIcon,
                0
            )
        }

    }

    override fun getItemCount(): Int = list.size

    inner class SettingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    interface OnClickListener {
        fun itemClicked(pos: Int, id: Int)
    }
}