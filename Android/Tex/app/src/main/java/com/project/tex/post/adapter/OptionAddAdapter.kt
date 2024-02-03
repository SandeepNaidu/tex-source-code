package com.project.tex.post.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.tex.databinding.OptionItemLayoutBinding

class OptionAddAdapter(private val list: MutableList<OptionData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val map = HashMap<Int, TextWatcherCustom>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TextViewHolder(
            OptionItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), TextWatcherCustom()
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TextViewHolder) {
            holder.view.root.tag = position
            holder.watcher.updatePosition(position)
            holder.watcher.updateData(list[position])
            holder.bind(list[position])
        }
    }

    fun addOption() {
        if (list.size < 8) {
            list.add(OptionData("Option ${list.size + 1}", ""))
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = list.size

    inner class TextViewHolder(
        val view: OptionItemLayoutBinding,
        val watcher: TextWatcherCustom
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(s: OptionData) {
            if (watcher != null) {
                view.optionNEdt.removeTextChangedListener(watcher)
            }
            view.optionNEdt.setText(s.optionValue)
            view.optionNTil.setHint(s.optionText)
            if (watcher != null) {
                view.optionNEdt.addTextChangedListener(watcher)
            }
        }
    }

    fun getOptionData(): MutableList<OptionData> {
        return list
    }

    data class OptionData(
        val optionText: String,
        var optionValue: String
    )

    class TextWatcherCustom : TextWatcher {
        var position: Int = 0
        var optionData: OptionData? = null

        fun updatePosition(pos: Int) {
            this.position = pos
        }

        fun updateData(optionData: OptionData) {
            this.optionData = optionData
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            optionData?.optionValue = s.toString()
        }

    }
}