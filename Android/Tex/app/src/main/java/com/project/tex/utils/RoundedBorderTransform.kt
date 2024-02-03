package com.project.tex.utils

import android.graphics.*

class RoundedBorderTransform(private val radius: Int, private val margin: Int) :
    com.squareup.picasso.Transformation {

    override fun transform(source: Bitmap?): Bitmap {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawCircle(
            (source.width - margin) / 2f,
            (source.height - margin) / 2f,
            radius - 2f,
            paint
        )

        if (source != output) {
            source.recycle()
        }

        val borderPaint = Paint()
        borderPaint.color = Color.RED
        borderPaint.style = Paint.Style.STROKE
        borderPaint.isAntiAlias = true
        borderPaint.strokeWidth = 2f
        canvas.drawCircle(
            (source.width - margin) / 2f,
            (source.height - margin) / 2f,
            radius - 2f,
            borderPaint
        )

        return output
    }

    override fun key(): String {
        return "roundedBorder"
    }
}

