package com.project.tex.utils

import kotlin.math.absoluteValue

object RangeExts {
    fun IntRange.toIntArray(): IntArray {
        if (last < first)
            return IntArray(0)

        val result = IntArray(last - first + 1)
        var index = 0
        for (element in this)
            result[index++] = element
        return result
    }

    fun IntRange.toStringArray(): ArrayList<String> {
        val result = arrayListOf<String>()
        if (last < first) {
            for (element in first downTo last)
                result.add(element.toString())
        } else {
            for (element in this)
                result.add(element.toString())
        }
        return result
    }
}