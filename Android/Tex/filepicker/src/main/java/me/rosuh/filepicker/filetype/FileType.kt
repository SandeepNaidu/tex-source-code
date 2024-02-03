package me.rosuh.filepicker.filetype

interface FileType {
    val fileType: String

    val fileIconResId: Int

    fun verify(fileName: String): Boolean
}