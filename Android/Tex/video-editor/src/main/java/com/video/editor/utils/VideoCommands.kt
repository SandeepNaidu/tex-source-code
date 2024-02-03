package com.video.editor.utils

import android.net.Uri
import com.arthenica.ffmpegkit.ExecuteCallback
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.arthenica.ffmpegkit.Session
import com.video.editor.interfaces.OnCommandVideoListener

class VideoCommands {
    companion object {
        private const val TAG = "VideoCommands"
    }

    fun trimVideo(
        startPos: String,
        endPos: String,
        input: String,
        output: String,
        outputFileUri: Uri,
        listener: OnCommandVideoListener?
    ) {
        FFmpegKit.executeAsync(
            "-y -i \"$input\" -ss 00:${startPos} -to 00:${endPos} -c copy \"$output\""
        ) { session ->
            if (ReturnCode.isSuccess(session?.returnCode)) {
                listener?.getResult(outputFileUri)
            } else if (ReturnCode.isCancel(session?.returnCode)) {
                listener?.cancelAction()
            } else {
                session?.failStackTrace?.let { listener?.onError(it) }
            }
        }
//        ffmpeg.loadBinary(object : FFmpegLoadBinaryResponseHandler {
//            override fun onFinish() {
//                Log.d("FFmpeg", "onFinish")
//            }
//
//            override fun onSuccess() {
//                val command = arrayOf(
//                    "-y",
//                    "-i",
//                    input,
//                    "-ss",
//                    startPos,
//                    "-to",
//                    endPos,
//                    "-c",
//                    "copy",
//                    output
//                )
//                try {
//                    ffmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
//                        override fun onSuccess(message: String?) {
//                            super.onSuccess(message)
//                        }
//
//                        override fun onProgress(message: String?) {
//                            super.onProgress(message)
//                        }
//
//                        override fun onFailure(message: String?) {
//                            super.onFailure(message)
//                            listener?.onError(message.toString())
//                        }
//
//                        override fun onStart() {
//                            super.onStart()
//                        }
//
//                        override fun onFinish() {
//                            super.onFinish()
//                        }
//                    })
//                } catch (e: FFmpegCommandAlreadyRunningException) {
//                    listener?.onError(e.toString())
//                }
//            }
//
//            override fun onFailure() {
//                listener?.onError("Failed")
//            }
//
//            override fun onStart() {
//            }
//        })
        listener?.onStarted()
    }

//    fun cropVideo(
//        w: Int,
//        h: Int,
//        x: Int,
//        y: Int,
//        input: String,
//        output: String,
//        outputFileUri: Uri,
//        listener: OnCommandVideoListener?
//    ) {
//        val ffmpeg = FFmpeg.getInstance(ctx)
//        ffmpeg.loadBinary(object : FFmpegLoadBinaryResponseHandler {
//            override fun onFinish() {
//                Log.d("FFmpeg", "onFinish")
//            }
//
//            override fun onSuccess() {
//                val command = arrayOf(
//                    "-i",
//                    input,
//                    "-filter:v",
//                    "crop=$w:$h:$x:$y",
//                    "-threads",
//                    "5",
//                    "-preset",
//                    "ultrafast",
//                    "-strict",
//                    "-2",
//                    "-c:a",
//                    "copy",
//                    output
//                )
//                try {
//                    ffmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
//                        override fun onSuccess(message: String?) {
//                            super.onSuccess(message)
//                        }
//
//                        override fun onFailure(message: String?) {
//                            super.onFailure(message)
//                            listener?.onError(message.toString())
//                        }
//
//                        override fun onFinish() {
//                            super.onFinish()
//                            listener?.getResult(outputFileUri)
//                        }
//                    })
//                } catch (e: FFmpegCommandAlreadyRunningException) {
//                    listener?.onError(e.toString())
//                }
//            }
//
//            override fun onFailure() {
//                Log.d("FFmpegLoad", "onFailure")
//                listener?.onError("Failed")
//            }
//
//            override fun onStart() {
//            }
//        })
//        listener?.onStarted()
//    }

}