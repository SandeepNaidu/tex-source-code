package com.project.tex.utils

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.project.tex.R

class MyClickableSpanUnderline(
    private val context: Context,
    private val clickListener: View.OnClickListener
) :
    ClickableSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = true
        ds.color = ContextCompat.getColor(context, R.color.colorPrimary)
        ds.typeface = ResourcesCompat.getFont(context, R.font.gilroy_medium)
    }

    override fun onClick(tv: View) {
        clickListener.onClick(tv)
    }
}
