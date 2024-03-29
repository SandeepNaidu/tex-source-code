package me.rosuh.filepicker.engine

import android.content.Context
import android.util.Log
import android.widget.ImageView
import me.rosuh.filepicker.R
import me.rosuh.filepicker.config.FilePickerManager

/**
 * A global image loading controller, including determining whether there is and what kind of
 * image loading engine exists.
 */
object ImageLoadController {
    private val enableGlide by lazy {
        try {
            Class.forName("com.bumptech.glide.Glide")
            true
        } catch (e: ClassNotFoundException) {
            false
        } catch (e: ExceptionInInitializerError) {
            false
        }
    }

    private val enablePicasso by lazy {
        try {
            Class.forName("com.squareup.picasso.Picasso")
            true
        } catch (e: ClassNotFoundException) {
            false
        } catch (e: ExceptionInInitializerError) {
            false
        }
    }

    private var engine: ImageEngine? = null

    /**
     * Load images, if there is no image loading engine, then the default icon is used
     */
    fun load(
        context: Context,
        iv: ImageView,
        url: String,
        placeholder: Int? = R.drawable.ic_unknown_file_picker
    ) {
        if (engine == null && !initEngine()) {
            iv.setImageResource(placeholder ?: R.drawable.ic_unknown_file_picker)
            return
        }
        try {
            engine?.loadImage(context, iv, url, placeholder ?: R.drawable.ic_unknown_file_picker)
        } catch (e: NoSuchMethodError) {
            Log.d(
                "ImageLoadController", """
                AndroidFilePicker throw NoSuchMethodError which means current Glide version was not supported. 
                We recommend using 4.9+ or you should implements your own ImageEngine.
                Ref:https://github.com/rosuH/AndroidFilePicker/issues/76
            """.trimIndent()
            )
            iv.setImageResource(placeholder ?: R.drawable.ic_unknown_file_picker)
        }
    }

    /**
     * Every time the configuration is updated, we need to re-initialize the image loader
     */
    private fun initEngine(): Boolean {
        engine = when {
            FilePickerManager.config.customImageEngine != null -> {
                FilePickerManager.config.customImageEngine
            }
            enableGlide -> {
                GlideEngine()
            }
            enablePicasso -> {
                PicassoEngine()
            }
            else -> {
                null
            }
        }
        return engine != null
    }

    fun reset() {
        engine = null
    }
}