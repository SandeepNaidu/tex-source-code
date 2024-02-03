package com.project.tex.main.ui.avid.avidCamera.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import com.project.tex.R
import com.project.tex.databinding.SelfPromptItemLayoutBinding

class SelfPromptTextAdapter(val list: MutableList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var selectedPos: Int = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TextViewHolder(
            SelfPromptItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ).root
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TextViewHolder) {
            holder.itemView.findViewById<CheckedTextView>(R.id.text).apply {
                isChecked = selectedPos == position
            }

            holder.itemView.findViewById<CheckedTextView>(R.id.text).apply {
                text = list[position]
            }
        }
    }

    public fun updateList(nList: MutableList<String>) {
        list.clear()
        list.addAll(nList)
        notifyDataSetChanged()
    }

    public fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    inner class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindText() {

        }
    }

}