package com.project.tex.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import com.google.android.material.textfield.TextInputEditText

object AnimUtils {
    fun TextInputEditText.smoothUpateTextSize(from: Int, to: Int) {
        val start = ViewUtils.dpToPx(context, from) / resources.displayMetrics.density
        val end = ViewUtils.dpToPx(context, to) / resources.displayMetrics.density
        if (textSize == end) return
        val animator: ValueAnimator = ObjectAnimator.ofFloat(
            this,
            "textSize",
            start,
            end
        )
        animator.duration = 400
        animator.start()
        animator.addUpdateListener {
            Log.d("AnimUtils", "smoothUpateTextSize: ${it.animatedValue}")
        }
    }
}