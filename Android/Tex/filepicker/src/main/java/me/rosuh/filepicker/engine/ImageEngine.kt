package me.rosuh.filepicker.engine

import android.content.Context
import android.net.Uri
import android.widget.ImageView

/**
 * Describe the interface of the picture loader for use by Glide, Picasso or other loaders
 */
interface ImageEngine {
    /**
     * Call this interface to load the picture. Generally, the [url] parameter indicates the local
     * path of the picture, and the value obtained through [Uri.parse].
     * Usually starts with file:///
     * If loading fails, [placeholder] will be used
     */
    fun loadImage(
        context: Context?,
        imageView: ImageView?,
        url: String?,
        placeholder: Int
    )
}