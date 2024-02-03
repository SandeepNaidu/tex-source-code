package me.rosuh.filepicker.filetype

import me.rosuh.filepicker.R

class DocumentFileType : FileType {

    override val fileType: String
        get() = "Document"
    override val fileIconResId: Int
        get() = R.drawable.ic_compressed_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix) {
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1)
        return when (suffix) {
            "pdf" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}