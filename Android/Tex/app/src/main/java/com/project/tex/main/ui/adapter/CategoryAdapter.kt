package com.project.tex.main.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.tex.R
import com.project.tex.databinding.CategoryItemLayoutBinding

class CategoryAdapter(val list:MutableList<String>, val categoryListener: CategoryListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var selectedPos: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TextViewHolder(
            CategoryItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ).root
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TextViewHolder) {
            holder.itemView.findViewById<CheckedTextView>(R.id.text).apply {
                isChecked = selectedPos == position
                setTextColor(
                    ContextCompat.getColor(
                        context, if (isChecked) R.color.colorPrimary else R.color.text_light
                    )
                )
            }

            holder.itemView.findViewById<CheckedTextView>(R.id.text).apply {
                text = list.get(position)
                setOnClickListener {
                    selectedPos = position
                    categoryListener.categorySelected(selectedPos)
                }
            }
        }
    }

    override fun getItemCount(): Int = 6

    inner class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    interface CategoryListener{
        fun categorySelected(pos: Int)
    }
}