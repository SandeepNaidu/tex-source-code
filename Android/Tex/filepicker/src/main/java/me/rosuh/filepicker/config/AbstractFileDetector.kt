package me.rosuh.filepicker.config

import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.filetype.FileType

/**
 *
 * ===================================================================================================================
 * This class is used to register your own file type detection methods. You need to follow the following steps:
 * 1. implement your own file type [FileType], which is the [FileType.verify] method of the [FileType].
 * 2. Construct a subclass of this class and, in [AbstractFileDetector.fillFileType], detect the file type and assign it to [FileItemBeanImpl.fileType] property.
 *
 */
abstract class AbstractFileDetector {
    abstract fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl
}