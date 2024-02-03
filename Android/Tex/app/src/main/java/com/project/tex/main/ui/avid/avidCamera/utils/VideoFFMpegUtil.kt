package com.project.tex.main.ui.avid.avidCamera.utils

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession

object VideoFFMpegUtil {
    fun trimVideo(
        inFile: String,
        outFile: String,
        startDurationMillis: Long = 0,
        endDurationMillis: Long = 0
    ): FFmpegSession? {
        return FFmpegKit.execute("-y -ss ${convertSecondsToHMmSs(startDurationMillis / 1000)} -i \"$inFile\" -to ${convertSecondsToHMmSs(endDurationMillis / 1000)} -async 1 -strict -2 -c copy \"$outFile\"")
    }

    private fun convertSecondsToHMmSs(seconds: Long): String {
        val s = seconds % 60
        val m = seconds / 60 % 60
        val h = seconds / (60 * 60) % 24
        return String.format("%d:%02d:%02d", h, m, s)
    }
}