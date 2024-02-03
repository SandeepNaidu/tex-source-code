package com.project.tex.recruiter.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.project.tex.R
import com.project.tex.databinding.Step1ProfileLayoutBinding
import com.project.tex.databinding.Step2ProfileLayoutBinding
import com.project.tex.utils.ViewUtils
import com.project.tex.utils.ViewUtils.setBackgroundColorSelector

class ProfileFormAdapter(val list: MutableList<Int>) : PagerAdapter() {

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val item = when (list.get(position)) {
            0 -> {
                addEditableField(parent)
            }
            1 -> {
                addNonEditableField(parent)
            }
            2 -> {
                addSingleSelectorList(
                    parent,
                    listOf(
                        "Singer",
                        "Acting",
                        "Music",
                        "Dancing",
                        "Sound",
                        "Writing"
                    )
                )
            }
            else -> {
                addEditableField(parent)
            }
        }
        parent.addView(item)
        return item
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int = list.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    private fun addEditableField(root: View): View {
        val step1ProfileLayoutBinding =
            Step1ProfileLayoutBinding.inflate(LayoutInflater.from(root.context!!))
        return step1ProfileLayoutBinding.root
    }

    private fun addNonEditableField(root: View): LinearLayout {
        val step2ProfileLayoutBinding =
            Step2ProfileLayoutBinding.inflate(LayoutInflater.from(root.context!!))
        return step2ProfileLayoutBinding.root
    }

    private fun addSingleSelectorList(root: View, list: List<String>): ChipGroup {
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
        return chipLayout
    }

    fun remove(currentPage: Int) {
        val pageValue = list[currentPage]
        if (pageValue < 0) return
        list.remove(pageValue)
        notifyDataSetChanged()
    }

}