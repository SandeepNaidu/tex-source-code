package me.rosuh.filepicker.filetype

import me.rosuh.filepicker.R

class VideoFileType : FileType {

    override val fileType: String
        get() = "Video"
    override val fileIconResId: Int
        get() = R.drawable.ic_video_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix) {
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1)
        return when (suffix) {
            "mp4", "mkv", "mov", "mpg", "mpeg", "3gp",
            "3gpp", "3g2", "3gpp2", "webm", "ts", "avi",
            "flv", "swf", "wmv", "vob", "m4v" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}