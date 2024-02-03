package com.project.tex.addmedia

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.WorkerThread
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

object FileUtils {
    private const val TAG = "FileUtils"

    @WorkerThread
    fun getReadablePathFromUri(context: Context, uri: Uri): String? {
        var path: String? = null
        if ("file".equals(uri.scheme, ignoreCase = true)) {
            path = uri.path
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            path = getPath(context, uri)
        }
        if (TextUtils.isEmpty(path)) {
            return path
        }
        Log.d(TAG, "get path from uri: $path")
        if (!isReadablePath(path)) {
            val index = path!!.lastIndexOf("/")
            val name = path.substring(index + 1)
            val dstPath = context.cacheDir.absolutePath + File.separator + name
            if (copyFile(context, uri, dstPath)) {
                path = dstPath
                Log.d(TAG, "copy file success: $path")
            } else {
                Log.d(TAG, "copy file fail!")
            }
        }
        return path
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                Log.d("External Storage", docId)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val dstPath =
                    context.cacheDir.absolutePath + File.separator + getFileName(context, uri)
                if (copyFile(context, uri, dstPath)) {
                    Log.d(TAG, "copy file success: $dstPath")
                    return dstPath
                } else {
                    Log.d(TAG, "copy file fail!")
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else if ("document" == type) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentUri = MediaStore.getMediaUri(context, uri)
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return uri.path
    }

    private fun isVirtualFile(
        uri: Uri,
        context: Context,
        contentResolver: ContentResolver
    ): Boolean {
        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return false
        }

        val cursor: Cursor? = contentResolver.query(
            uri,
            arrayOf(DocumentsContract.Document.COLUMN_FLAGS),
            null,
            null,
            null
        )

        val flags: Int = cursor?.use {
            if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
        } ?: 0

        return flags and DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT != 0
    }

    fun getFileName(context: Context, uri: Uri?): String {
        val cursor = context.contentResolver.query(uri!!, null, null, null, null)
        val nameindex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(nameindex)
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isReadablePath(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }
        val isLocalPath: Boolean
        isLocalPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!TextUtils.isEmpty(path)) {
                val localFile = File(path)
                localFile.exists() && localFile.canRead()
            } else {
                false
            }
        } else {
            path!!.startsWith(File.separator)
        }
        return isLocalPath
    }

    private fun copyFile(context: Context, uri: Uri, dstPath: String): Boolean {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            outputStream = FileOutputStream(dstPath)
            val buff = ByteArray(100 * 1024)
            var len: Int
            while (inputStream!!.read(buff).also { len = it } != -1) {
                outputStream.write(buff, 0, len)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return true
    }
}