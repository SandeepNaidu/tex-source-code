package com.project.tex.utils

import android.util.Log
import java.text.ParseException
import java.util.*
import java.util.concurrent.TimeUnit

object TimeLeft2 {
    public fun covertTimeToText(dataDate: String?, addDays: Int?): String? {
        if (dataDate == null) return ""
        var convTime: String? = null
        val prefix = ""
        val suffix = "Left"
        try {
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ENGLISH)
//            val pasTime = dateFormat.parse(dataDate)
            val c = Calendar.getInstance()
            c.timeInMillis = dataDate.toLongOrNull() ?: 0
//            if (pasTime != null) {
//                c.time = pasTime
//            }
            c.add(Calendar.DATE, addDays ?: 0)

            val nowTime = Date()
            val dateDiff = c.timeInMillis - nowTime.time
            val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                convTime =
                    "" + if (second > 1) "$second Seconds $suffix" else " Over "
            } else if (minute < 60) {
                convTime = "$minute" + (if (minute > 1) " Minutes " else " Minute ") + suffix
            } else if (hour < 24) {
                convTime = "$hour" + (if (hour > 1) " Hours " else " Hour ") + suffix
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
                convTime = "$day " + (if (day > 1) " Days " else " Day ") + suffix
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
            return ""
        }
        return convTime
    }

    fun getLefttime(dataDate: String?, addDays: Int?): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = dataDate?.toLongOrNull() ?: 0
        c.add(Calendar.DATE, addDays ?: 0)

        val nowTime = Date()
        val dateDiff =  c.timeInMillis - nowTime.time
        return dateDiff
    }
}