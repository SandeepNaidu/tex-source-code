package com.project.tex.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.project.tex.R
import com.project.tex.databinding.ItemFaqListBinding
import com.project.tex.settings.viewmodel.FaqItem

class FaqAdapter(val list: List<FaqItem>) :
    RecyclerView.Adapter<FaqAdapter.FAQViewHolder>() {
    var selectedPos: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        return FAQViewHolder(
            ItemFaqListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.view.quest.apply {
            text = list.get(position).question
            val drawable =
                if (position == selectedPos) R.drawable.up_arrow else R.drawable.down_arrow
            setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawable,0)
            setOnClickListener {
                selectedPos = position
                notifyDataSetChanged()
            }
        }
        holder.view.answer.apply {
            text = list.get(position).answer
            isVisible = position == selectedPos
        }

    }

    override fun getItemCount(): Int = list.size

    inner class FAQViewHolder(val view: ItemFaqListBinding) : RecyclerView.ViewHolder(view.root) {

    }
}