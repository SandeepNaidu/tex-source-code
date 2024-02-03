package com.project.tex.recruiter.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.project.tex.R
import com.project.tex.databinding.ProfileFormItemLayoutBinding
import com.project.tex.databinding.Step1ProfileLayoutBinding
import com.project.tex.databinding.Step2ProfileLayoutBinding
import com.project.tex.utils.ViewUtils
import com.project.tex.utils.ViewUtils.setBackgroundColorSelector

class RecruiterHistoryAdapter(val formClickListener: FormClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BaseHistoryCardViewHolder(View(parent.context))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is ProfileViewHolder) {
//            holder.bindUi(position)
//            when (position) {
//                0 -> {
//                    (holder.view as ProfileFormItemLayoutBinding).addEditableField()
//                }
//                1 -> {
//                    (holder.view as ProfileFormItemLayoutBinding).addNonEditableField()
//                }
//                2 -> {
//                    (holder.view as ProfileFormItemLayoutBinding).addSingleSelectorList(
//                        listOf(
//                            "All",
//                            "Singer",
//                            "Acting",
//                            "Music",
//                            "Dancing",
//                            "Sound",
//                            "Writing"
//                        )
//                    )
//                }
//                3 -> {
//
//                }
//            }
//        }
    }

    override fun getItemCount(): Int = 8

    inner class BaseHistoryCardViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        fun bindUi(position: Int) {
//            (view as ProfileFormItemLayoutBinding).apply {
//                stepperTv.text = "${position + 1}/8"
//                dotsIndicator.setDotProgressiveSelection(position)
//                btnNext.setOnClickListener(this@ProfileViewHolder)
//                btnSkip.setOnClickListener(this@ProfileViewHolder)
//                rightArrowBtn.setOnClickListener(this@ProfileViewHolder)
//                leftArrowBtn.setOnClickListener(this@ProfileViewHolder)
//            }

        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.right_arrow_btn -> {
                    formClickListener.doAction(absoluteAdapterPosition)
                }
            }
        }

    }

    private fun ProfileFormItemLayoutBinding.addEditableField() {
        val step1ProfileLayoutBinding =
            Step1ProfileLayoutBinding.inflate(LayoutInflater.from(root.context!!))
        postFrame.addView(step1ProfileLayoutBinding.root)
    }

    private fun ProfileFormItemLayoutBinding.addNonEditableField() {
        val step2ProfileLayoutBinding =
            Step2ProfileLayoutBinding.inflate(LayoutInflater.from(root.context!!))
        postFrame.addView(step2ProfileLayoutBinding.root)
    }

    private fun ProfileFormItemLayoutBinding.addSingleSelectorList(list: List<String>) {
        val chipLayout = ChipGroup(root.context)
        chipLayout.setBackgroundColor(Color.WHITE)
        chipLayout.setPadding(ViewUtils.dpToPx(root.context, 16))
        list.forEach { text ->
            val chip = Chip(root.context)
            chip.setText(text)
            chip.isCheckable = true
            chipLayout.addView(chip)
            chip.setBackgroundColorSelector(
                ContextCompat.getColor(root.context, R.color.category_selected_bg),
                ContextCompat.getColor(root.context, R.color.drawable_outline_grey)
            )
            chip.chipEndPadding = ViewUtils.dpToPx(root.context, 8).toFloat()
            chip.typeface = ResourcesCompat.getFont(root.context, R.font.gilroy_medium)
            chip.chipCornerRadius = ViewUtils.dpToPx(context = root.context, 20).toFloat()
        }
        postFrame.addView(chipLayout)
    }

    interface FormClickListener {
        fun doAction(position: Int)
    }
}