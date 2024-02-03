package com.project.tex.utils

import android.util.Log
import androidx.core.text.isDigitsOnly
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TimeAgo2 {
    public fun covertTimeToText(dataDate: String?): String? {
        if (dataDate == null) return ""
        var convTime: String? = null
        val prefix = ""
        val suffix = "Ago"
        try {
            val nowTime = Date()
            val dateDiff = if(!dataDate.isDigitsOnly()) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ENGLISH)
                val pasTime = dateFormat.parse(dataDate)
                nowTime.time - pasTime!!.time
            } else {
                nowTime.time - (dataDate.toLongOrNull() ?: nowTime.time)
            }
            val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                convTime = "$second" + (if (second > 1) " Seconds " else " Second ") + "$suffix"
            } else if (minute < 60) {
                convTime = "$minute" + (if (minute > 1) " Minutes " else " Minute ") + "$suffix"
            } else if (hour < 24) {
                convTime = "$hour" + (if (hour > 1) " Hours " else " Hour ") + "$suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    val a = (day / 360)
                    a.toString() + (if (a > 1) " Years " else " Year ") + suffix
                } else if (day > 30) {
                    val a = (day / 30)
                    a.toString() + (if (a > 1) " Months " else " Month ") + suffix
                } else {
                    val a = (day / 7)
                    a.toString() + (if (a > 1) " Weeks " else " Week ") + suffix
                }
            } else if (day < 7) {
                convTime = "$day " + (if (day > 1) " Days " else " Day ") + "$suffix"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
        }
        return convTime
    }
}