package com.project.tex.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.text.*
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils.TruncateAt.END
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.project.tex.R
import kotlin.math.abs

class ReadMoreTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var originalText: String = ""
        set(value) {
            field = value
            updateCollapsedDisplayedText(ctaChanged = false)
        }
    var expandAction: String = ""
        set(value) {
            field = value
            val ellipsis = Typography.ellipsis
            val start = ellipsis.toString().length
            expandActionSpannable = SpannableString(" $ellipsis $value")
            expandActionSpannable.setSpan(
                ForegroundColorSpan(expandActionColor),
                start,
                expandActionSpannable.length,
                SPAN_EXCLUSIVE_EXCLUSIVE
            )
            expandActionSpannable.setSpan(
                CustomTypefaceSpan(
                    "Gilbroy", ResourcesCompat.getFont(context, R.font.gilroy_medium)!!
                ), start, expandActionSpannable.length, SPAN_EXCLUSIVE_EXCLUSIVE
            )
            updateCollapsedDisplayedText(ctaChanged = true)
        }
    var limitedMaxLines: Int = 3
        set(value) {
            check(maxLines == -1 || value <= maxLines) {
                """
                    maxLines ($maxLines) must be greater than or equal to limitedMaxLines ($value). 
                    maxLines can be -1 if there is no upper limit for lineCount.
                """.trimIndent()
            }
            field = value
            updateCollapsedDisplayedText(ctaChanged = false)
        }

    var collapseActionText: String = ""
        set(value) {
            field = value
            collapseActionSpan = SpannableString("$originalText $value")
            collapseActionSpan.setSpan(
                CustomTypefaceSpan(
                    "Gilbroy", ResourcesCompat.getFont(context, R.font.gilroy_medium)!!
                ), originalText.length, collapseActionSpan.length, SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    @ColorInt
    var expandActionColor: Int = ContextCompat.getColor(context, android.R.color.holo_purple)
        set(value) {
            field = value
            val colorSpan = ForegroundColorSpan(value)
            val ellipsis = Typography.ellipsis
            val start = ellipsis.toString().length
            expandActionSpannable.setSpan(
                colorSpan, start, expandActionSpannable.length, SPAN_EXCLUSIVE_EXCLUSIVE
            )
            expandActionSpannable.setSpan(
                CustomTypefaceSpan(
                    "Gilbroy", ResourcesCompat.getFont(context, R.font.gilroy_medium)!!
                ), start, expandActionSpannable.length, SPAN_EXCLUSIVE_EXCLUSIVE
            )
            updateCollapsedDisplayedText(ctaChanged = true)
        }

    var collapsed = true
        private set
    val expanded get() = !collapsed

    private var oldTextWidth = 0
    private var animator: Animator? = null
    private var expandActionSpannable = SpannableString("")
    private var expandActionStaticLayout: StaticLayout? = null
    private var collapsedDisplayedText: CharSequence? = null
    private var collapseActionSpan = SpannableString("")


    init {
        ellipsize = END
        val a = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreTextView)
        expandAction = a.getString(R.styleable.ReadMoreTextView_expandAction) ?: expandAction
        expandActionColor =
            a.getColor(R.styleable.ReadMoreTextView_expandActionColor, expandActionColor)
        originalText = a.getString(R.styleable.ReadMoreTextView_originalText) ?: originalText
        collapseActionText =
            a.getString(R.styleable.ReadMoreTextView_collapseAction) ?: collapseActionText
        limitedMaxLines = a.getInt(R.styleable.ReadMoreTextView_limitedMaxLines, limitedMaxLines)
        check(maxLines == -1 || limitedMaxLines <= maxLines) {
            """
                maxLines ($maxLines) must be greater than or equal to limitedMaxLines ($limitedMaxLines). 
                maxLines can be -1 if there is no upper limit for lineCount.
            """.trimIndent()
        }
        a.recycle()
        setOnClickListener { toggle() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val givenWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textWidth = givenWidth - compoundPaddingStart - compoundPaddingEnd
        if (textWidth == oldTextWidth || animator?.isRunning == true) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        oldTextWidth = textWidth
        updateCollapsedDisplayedText(ctaChanged = true, textWidth)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun setMaxLines(maxLines: Int) {
        check(maxLines == -1 || limitedMaxLines <= maxLines) {
            """
                maxLines ($maxLines) must be greater than or equal to limitedMaxLines ($limitedMaxLines). 
                maxLines can be -1 if there is no upper limit for lineCount.
            """.trimIndent()
        }
        super.setMaxLines(maxLines)
        updateCollapsedDisplayedText(ctaChanged = false)
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun setEllipsize(where: TextUtils.TruncateAt?) {
        /**
         * Due to this issue https://stackoverflow.com/questions/63939222/constraintlayout-ellipsize-start-not-working,
         * this view only supports TextUtils.TruncateAt.END
         */
        super.setEllipsize(END)
    }

    fun setCollapseText() {
        collapseActionSpan.setSpan(
            CustomTypefaceSpan(
                "Gilbroy", ResourcesCompat.getFont(context, R.font.gilroy_bold)!!
            ), originalText.length, collapseActionSpan.length - 1, SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun toggle() {
        if (originalText == collapsedDisplayedText) {
            collapsed = !collapsed
            return
        }
        val height0 = height
        text = if (collapsed) collapseActionSpan else collapsedDisplayedText
        measure(
            MeasureSpec.makeMeasureSpec(width, EXACTLY),
            MeasureSpec.makeMeasureSpec(height, UNSPECIFIED)
        )
        val height1 = measuredHeight
        animator?.cancel()
        val dur = (abs(height1 - height0) * 2L).coerceAtMost(300L)
        animator = ValueAnimator.ofInt(height0, height1).apply {
            interpolator = FastOutSlowInInterpolator()
            duration = dur
            addUpdateListener { value ->
                val params = layoutParams
                layoutParams.height = value.animatedValue as Int
                layoutParams = params
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    collapsed = !collapsed
                    text = collapseActionSpan
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    text = if (collapsed) collapsedDisplayedText else collapseActionSpan
                    val params = layoutParams
                    layoutParams.height = WRAP_CONTENT
                    layoutParams = params
                }
            })
            start()
        }
    }

    private fun resolveDisplayedText(staticLayout: StaticLayout): CharSequence? {
        val truncatedTextWithoutCta = staticLayout.text
        if (truncatedTextWithoutCta.toString() != originalText) {
            val totalTextWidthWithoutCta =
                (0 until staticLayout.lineCount).sumOf { staticLayout.getLineWidth(it).toInt() }
            val totalTextWidthWithCta =
                totalTextWidthWithoutCta - expandActionStaticLayout!!.getLineWidth(0)
            val textWithoutCta =
                TextUtils.ellipsize(originalText, paint, totalTextWidthWithCta, END)
            val defaultEllipsisStart = textWithoutCta.indexOf(Typography.ellipsis)
            // in case the size only fits cta, shows cta only
            if (textWithoutCta == "") return expandActionStaticLayout!!.text
            // on some devices Typography.ellipsis can't be found,
            // in that case don't replace ellipsis sign with ellipsizedText
            // users are still able to expand ellipsized text
            if (defaultEllipsisStart == -1) {
                return truncatedTextWithoutCta
            }
            val defaultEllipsisEnd = defaultEllipsisStart + 1
            val span = SpannableStringBuilder().append(textWithoutCta)
                .replace(defaultEllipsisStart, defaultEllipsisEnd, expandActionStaticLayout!!.text)
            return maybeRemoveEndingCharacters(staticLayout, span)
        } else {
            return originalText
        }
    }

    // sanity check before applying the text. Most of the time, the loop doesn't happen
    private fun maybeRemoveEndingCharacters(
        staticLayout: StaticLayout,
        span: SpannableStringBuilder,
    ): SpannableStringBuilder {
        val textWidth = staticLayout.width
        val dynamicLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DynamicLayout.Builder.obtain(span, paint, textWidth).setAlignment(ALIGN_NORMAL)
                .setIncludePad(false).setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .build()
        } else {
            @Suppress("DEPRECATION") DynamicLayout(
                span,
                span,
                paint,
                textWidth,
                ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                false
            )
        }

        val ctaIndex = span.indexOf(expandActionStaticLayout!!.text.toString())
        var removingCharIndex = ctaIndex - 1
        while (removingCharIndex >= 0 && dynamicLayout.lineCount > limitedMaxLines) {
            span.delete(removingCharIndex, removingCharIndex + 1)
            removingCharIndex--
        }
        return span
    }

    private fun updateCollapsedDisplayedText(
        ctaChanged: Boolean,
        textWidth: Int = measuredWidth - compoundPaddingStart - compoundPaddingEnd,
    ) {
        if (textWidth <= 0) return
        val collapsedStaticLayout = getStaticLayout(limitedMaxLines, originalText, textWidth)
        if (ctaChanged) expandActionStaticLayout =
            getStaticLayout(1, expandActionSpannable, textWidth)
        collapsedDisplayedText = resolveDisplayedText(collapsedStaticLayout)
        text = if (collapsed) collapsedDisplayedText else collapseActionSpan
    }

    private fun getStaticLayout(
        targetMaxLines: Int, text: CharSequence, textWidth: Int
    ): StaticLayout {
        val maximumLineWidth = textWidth.coerceAtLeast(0)
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, maximumLineWidth)
            .setIncludePad(false).setEllipsize(END).setMaxLines(targetMaxLines)
            .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier).build()
    }

}