package me.rosuh.filepicker.filetype

import me.rosuh.filepicker.R

class RasterImageFileType : FileType {

    override val fileType: String
        get() = "Image"
    override val fileIconResId: Int
        get() = R.drawable.ic_image_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix) {
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1)
        return when (suffix) {
            "jpeg", "jpg", "bmp", "gif", "png" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}