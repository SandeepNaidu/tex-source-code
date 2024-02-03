package me.rosuh.filepicker.utils

import android.content.Context
import android.os.Environment
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.bean.FileNavBeanImpl
import me.rosuh.filepicker.config.FilePickerConfig.Companion.STORAGE_CUSTOM_ROOT_PATH
import me.rosuh.filepicker.config.FilePickerConfig.Companion.STORAGE_EXTERNAL_STORAGE
import me.rosuh.filepicker.config.FilePickerManager.config
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class FileUtils {

    companion object {

        /**
         * @return File
         */
        fun getRootFile(): File {
            return when (config.mediaStorageType) {
                STORAGE_EXTERNAL_STORAGE -> {
                    File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
                else -> {
                    File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
            }
        }

        fun produceListDataSource(
            rootFile: File
        ): ArrayList<FileItemBeanImpl> {
            val listData: ArrayList<FileItemBeanImpl> = ArrayList()
            var isDetected = false
            val realRoot = getRootFile()
            val isInRootParent = rootFile.list() == null
                    && !config.isSkipDir
                    && rootFile.path == realRoot.parentFile?.path
            if (isInRootParent) {
                listData.add(
                    FileItemBeanImpl(
                        realRoot.name,
                        realRoot.path,
                        false,
                        null,
                        isDir = true,
                        isHide = false
                    )
                )
                return config.selfFilter?.doFilter(listData) ?: listData
            }
            val listFiles = rootFile.listFiles()
            if (listFiles.isNullOrEmpty()) {
                return listData
            }
            for (file in listFiles) {
                val isHiddenFile = file.name.startsWith(".")
                if (!config.isShowHiddenFiles && isHiddenFile) {
                    // skip hidden files
                    continue
                }
                if (file.isDirectory) {
                    listData.add(
                        FileItemBeanImpl(
                            file.name,
                            file.path,
                            false,
                            null,
                            true,
                            isHiddenFile
                        )
                    )
                    continue
                }
                val itemBean = FileItemBeanImpl(
                    file.name,
                    file.path,
                    false,
                    null,
                    false,
                    isHiddenFile
                )
                config.defaultFileDetector.fillFileType(itemBean)
                isDetected = itemBean.fileType != null
                if (config.defaultFileDetector.enableCustomTypes
                    && config.isAutoFilter
                    && !isDetected
                ) {
                    // enable auto filter AND using user's custom file type. Filter them.
                    continue
                }
                listData.add(itemBean)
            }

            // Default sort by alphabet
            listData.sortWith(
                compareBy(
                    { !it.isDir },
                    { it.fileName.uppercase(Locale.getDefault()) })
            )
            // expose data list  to outside caller
            return config.selfFilter?.doFilter(listData) ?: listData
        }

        fun produceNavDataSource(
            currentDataSource: ArrayList<FileNavBeanImpl>,
            nextPath: String,
            context: Context
        ): ArrayList<FileNavBeanImpl> {
            if (currentDataSource.isEmpty()) {
                val dirName = getDirAlias(getRootFile())
                currentDataSource.add(
                    FileNavBeanImpl(
                        dirName,
                        nextPath
                    )
                )
                return currentDataSource
            }

            for (data in currentDataSource) {
                if (nextPath == currentDataSource.first().dirPath) {
                    return ArrayList(currentDataSource.subList(0, 1))
                }
                val isCurrent = nextPath == currentDataSource[currentDataSource.size - 1].dirPath
                if (isCurrent) {
                    return currentDataSource
                }

                val isBackToAbove = nextPath == data.dirPath
                if (isBackToAbove) {
                    return ArrayList(
                        currentDataSource.subList(
                            0,
                            currentDataSource.indexOf(data) + 1
                        )
                    )
                }
            }
            currentDataSource.add(
                FileNavBeanImpl(
                    nextPath.substring(nextPath.lastIndexOf("/") + 1),
                    nextPath
                )
            )
            return currentDataSource
        }

        fun getDirAlias(file: File): String {
            val isCustomRoot = config.mediaStorageType == STORAGE_CUSTOM_ROOT_PATH
                    && file.absolutePath == config.customRootPath
            val isPreSetStorageRoot = config.mediaStorageType == STORAGE_EXTERNAL_STORAGE
                    && file.absolutePath == getRootFile().absolutePath
            val isDefaultRoot = file.absolutePath == getRootFile().absolutePath
            return when {
                isCustomRoot || isPreSetStorageRoot -> {
                    config.mediaStorageName
                }
                isDefaultRoot -> {
                    config.defaultStorageName
                }
                else -> {
                    file.name
                }
            }
        }
    }
}