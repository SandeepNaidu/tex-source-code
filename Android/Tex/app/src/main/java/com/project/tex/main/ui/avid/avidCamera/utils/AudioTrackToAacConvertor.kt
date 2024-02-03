package com.project.tex.main.ui.avid.avidCamera.utils

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession


/**
 * This class converts audio from various audio formats that are supported by Android's decoders into
 * m4a/aac audio.
 * It is based on the examples from Android's CTS. For more information, please see
 * https://android.googlesource.com/platform/cts/+/jb-mr2-release/tests/tests/media/src/android/media/cts/DecodeEditEncodeTest.java
 */
object AudioTrackToAacConvertor {

    fun convertAudio(
        inFile: String,
        outFile: String,
        maxDurationMillis: Long = -1
    ): FFmpegSession? {
        return FFmpegKit.execute("-i \"$inFile\" -t ${maxDurationMillis.toFloat() / 1000} -c:a aac -b:a 128k \"$outFile\"")
    }

//    fun convertAudio(
//        ctx: Context,
//        inFile: String,
//        outFile: String,
//        maxDurationMillis: Long = -1, listener: OnCommandVideoListener?
//    ) {
//        val ffmpeg = FFmpeg.getInstance(ctx)
//        ffmpeg.loadBinary(object : FFmpegLoadBinaryResponseHandler {
//            override fun onStart() {
//                Log.d("FFmpeg", "onFinish")
//
//            }
//
//            override fun onFinish() {
//                Log.d("FFmpeg", "onFinish")
//            }
//
//            override fun onFailure() {
//            }
//
//            override fun onSuccess() {
//                val command = arrayOf(
//                    "-y",
//                    "-i",
//                    inFile,
//                    "-t",
//                    "${maxDurationMillis.toFloat() / 1000}",
//                    "-c:a",
//                    outFile
//                )
//                ffmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
//                    override fun onSuccess(message: String?) {
//                        super.onSuccess(message)
//                    }
//
//                    override fun onProgress(message: String?) {
//                        super.onProgress(message)
//                    }
//
//                    override fun onFailure(message: String?) {
//                        super.onFailure(message)
//                        listener?.onError(message.toString())
//                    }
//
//                    override fun onStart() {
//                        super.onStart()
//                    }
//
//                    override fun onFinish() {
//                        super.onFinish()
//                        listener?.getResult(Uri.parse(outFile))
//                    }
//                })
//            }
//        })
//    }
}