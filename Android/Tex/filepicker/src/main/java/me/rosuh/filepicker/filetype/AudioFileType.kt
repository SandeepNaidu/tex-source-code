package me.rosuh.filepicker.filetype

import me.rosuh.filepicker.R

/**
 *
 * 音频文件类型
 * @author rosu
 * @date 2018/11/27
 */
class AudioFileType : FileType {
    override val fileType: String
        get() = "Audio"
    override val fileIconResId: Int
        get() = R.drawable.ic_music_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix) {
            // 如果没有 . 符号，即是没有文件后缀
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1)
        return when (suffix) {
            "m3u", "m4a", "mid", "mp3", "mpa", "wav", "wma", "ogg", "flac", "ape", "alac" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}