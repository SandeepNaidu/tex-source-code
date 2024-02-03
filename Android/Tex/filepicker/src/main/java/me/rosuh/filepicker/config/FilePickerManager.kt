package me.rosuh.filepicker.config

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import me.rosuh.filepicker.engine.ImageLoadController
import java.lang.ref.WeakReference

object FilePickerManager {
    const val REQUEST_CODE = 10401

    internal var contextRef: WeakReference<FragmentActivity>? = null

    internal var fragmentRef: WeakReference<Fragment>? = null

    internal lateinit var config: FilePickerConfig

    private var dataList: MutableList<String> = ArrayList()

    @JvmStatic
    fun from(activity: FragmentActivity): FilePickerConfig {
        reset()
        this.contextRef = WeakReference(activity)
        config = FilePickerConfig(this)
        return config
    }

    @JvmStatic
    fun from(fragment: Fragment): FilePickerConfig {
        reset()
        this.fragmentRef = WeakReference(fragment)
        this.contextRef = WeakReference(fragment.activity)
        config = FilePickerConfig(this)
        return config
    }

    internal fun saveData(list: MutableList<String>) {
        dataList = list
    }

    /**
     * @return List<String>
     */
    @JvmOverloads
    @JvmStatic
    fun obtainData(release: Boolean = false): MutableList<String> {
        val result = ArrayList(dataList)
        if (release) {
            release()
        }
        return result
    }

    private fun reset() {
        contextRef?.clear()
        fragmentRef?.clear()
        dataList.clear()
        ImageLoadController.reset()
    }

    /**
     * Release resources and reset attributes
     */
    @JvmStatic
    fun release() {
        reset()
        if (this::config.isInitialized) {
            config.clear()
        }
    }
}