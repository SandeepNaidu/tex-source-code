package com.project.tex.aritist_profile

import android.text.InputFilter
import android.text.Spanned

class RangeInputFilter(min: Int, max: Int) : InputFilter {
    private val min: Int
    private val max: Int

    init {
        val rightOrder = min <= max
        this.min = if (rightOrder) min else max
        this.max = if (rightOrder) max else min
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dStart: Int,
        dEnd: Int
    ): CharSequence? {
        try {
            val sourceStr = source.toString()
            val destStr = dest.toString()
            val result = destStr.substring(0, dStart) + sourceStr.substring(
                start,
                end
            ) + destStr.substring(dEnd)
            val input = result.toInt()
            if (input in min..max) return null
        } catch (ignored: NumberFormatException) {
        }
        return ""
    }
}