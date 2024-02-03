package com.project.tex.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputLayout
import com.project.tex.R
import kotlin.math.roundToInt

object ViewUtils {
    fun dpToPx(context: Context, @Dimension(unit = Dimension.DP) dp: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        )
            .roundToInt()
    }

    @JvmName("dpToPx1")
    fun Context.dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).roundToInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            Resources.getSystem().displayMetrics
        )
    }

    fun TextInputLayout.setBoxStrokeColorSelector(
        @ColorInt color: Int,
        @ColorInt defaultColor: Int
    ) {

        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),// focused
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf() // default
        )

        val colors = intArrayOf(color, /*color,*/color, defaultColor)

        val myColorList = ColorStateList(states, colors)
        setBoxStrokeColorStateList(myColorList)
    }

    fun Chip.setBackgroundColorSelector(
        @ColorInt color: Int,
        @ColorInt defaultColor: Int
    ) {

        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),// focused
            intArrayOf(android.R.attr.state_checked), // enabled
            intArrayOf() // default
        )
        val statesText = arrayOf(
            intArrayOf(android.R.attr.state_focused),// focused
            intArrayOf(android.R.attr.state_checked), // enabled
            intArrayOf() // default
        )
        val text = ContextCompat.getColor(context, R.color.text_light)
        val textPrimary = ContextCompat.getColor(context, R.color.text_color_primary)
        val colors = intArrayOf(color, /*color,*/color, defaultColor)
        val colorsText = intArrayOf(textPrimary, /*color,*/textPrimary, text)

        val myColorList = ColorStateList(states, colors)
        val myColorListText = ColorStateList(statesText, colorsText)
        chipBackgroundColor = myColorList
        setTextColor(myColorListText)
        chipStrokeColor = myColorList
    }

}