package me.rosuh.filepicker.config

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import me.rosuh.filepicker.FilePickerActivity
import me.rosuh.filepicker.R
import me.rosuh.filepicker.engine.ImageEngine
import me.rosuh.filepicker.filetype.FileType
import java.io.File
import java.util.concurrent.*

class FilePickerConfig(private val pickerManager: FilePickerManager) {

    var isAutoFilter: Boolean = false

    private val customFileTypes: ArrayList<FileType> by lazy {
        ArrayList<FileType>(2)
    }

    private val contextRes = pickerManager.contextRef!!.get()!!.resources

    var isShowHiddenFiles = false
        private set

    var isShowingCheckBox = true
        private set

    var isSkipDir = true
        private set

    var singleChoice = false
        private set

    var maxSelectable = Int.MAX_VALUE
        private set

    internal val defaultStorageName = contextRes.getString(R.string.file_picker_tv_sd_card)

    var mediaStorageName = defaultStorageName
        private set

    @get:StorageMediaType
    @set:StorageMediaType
    var mediaStorageType: String = STORAGE_EXTERNAL_STORAGE
        private set

    var customRootPath: String = ""
        private set

    internal var customRootPathFile: File? = null
        private set

    var selfFilter: AbstractFileFilter? = null
        private set


    val defaultFileDetector: DefaultFileDetector by lazy { DefaultFileDetector().also { it.registerDefaultTypes() } }

    var itemClickListener: ItemClickListener? = null
        private set

    var themeId: Int = R.style.FilePickerThemeRail
        private set

    var selectAllText: String = contextRes.getString(R.string.file_picker_tv_select_all)
        private set
    var deSelectAllText: String = contextRes.getString(R.string.file_picker_tv_deselect_all)
        private set

    @StringRes
    var hadSelectedText: Int = R.string.file_picker_selected_count
        private set
    var confirmText: String = contextRes.getString(R.string.file_picker_tv_select_done)
        private set

    @StringRes
    var maxSelectCountTips: Int = R.string.max_select_count_tips
        private set

    var emptyListTips: String = contextRes.getString(R.string.empty_list_tips_file_picker)
        private set

    internal var threadPool: ExecutorService? = null

    /**
     * The custom thread pool will not be closed by default,
     * if you need to close when the file selection is finished, please pass true
     */
    internal var threadPoolAutoShutDown: Boolean = false

    var customImageEngine: ImageEngine? = null
        private set

    fun showHiddenFiles(isShow: Boolean): FilePickerConfig {
        isShowHiddenFiles = isShow
        return this
    }

    fun showCheckBox(isShow: Boolean): FilePickerConfig {
        isShowingCheckBox = isShow
        return this
    }

    fun skipDirWhenSelect(isSkip: Boolean): FilePickerConfig {
        isSkipDir = isSkip
        return this
    }

    fun maxSelectable(max: Int): FilePickerConfig {
        maxSelectable = if (max < 0) Int.MAX_VALUE else max
        return this
    }

    @JvmOverloads
    fun storageType(
        volumeName: String = "",
        @StorageMediaType storageMediaType: String
    ): FilePickerConfig {
        mediaStorageName = volumeName
        mediaStorageType = storageMediaType
        return this
    }

    fun setCustomRootPath(path: String): FilePickerConfig {
        customRootPath = path
        path.takeIf {
            it.isNotBlank()
        }?.let {
            File(it)
        }?.takeIf {
            it.exists()
        }?.let {
            customRootPathFile = it
        }
        return this
    }

    fun filter(fileFilter: AbstractFileFilter): FilePickerConfig {
        selfFilter = fileFilter
        return this
    }

    /**
     * Setting item click listener which can intercept click event.
     */
    fun setItemClickListener(
        itemClickListener: ItemClickListener
    ): FilePickerConfig {
        this.itemClickListener = itemClickListener
        return this
    }

    fun setTheme(themeId: Int): FilePickerConfig {
        this.themeId = themeId
        return this
    }

    fun enableSingleChoice(): FilePickerConfig {
        this.singleChoice = true
        return this
    }

    /**
     * Set the string of the interface, including:
     * Select all [selectAllString]
     * Uncheck [unSelectAllString]
     * Selected [hadSelectedStrRes]
     * Confirm [confirmText]
     * Multiple selection limit prompt: "You can only select 1 item" [maxSelectCountTipsStrRes]
     * Empty tries to look at the stereo: "empty as well" [emptyListTips]
     * Note:
     * [hadSelectedStrRes] and [maxSelectCountTipsStrRes] are strings of String format restrictions, you need to pass some string like [R.string.file_picker_selected_count]
     * The id in * and contains a placeholder for passing in an Int type parameter
     */
    fun setText(
        @NonNull selectAllString: String = contextRes.getString(R.string.file_picker_tv_select_all),
        @NonNull unSelectAllString: String = contextRes.getString(R.string.file_picker_tv_deselect_all),
        @NonNull @StringRes hadSelectedStrRes: Int = R.string.file_picker_selected_count,
        @NonNull confirmText: String = contextRes.getString(R.string.file_picker_tv_select_done),
        @NonNull @StringRes maxSelectCountTipsStrRes: Int = R.string.max_select_count_tips,
        @NonNull emptyListTips: String = contextRes.getString(R.string.empty_list_tips_file_picker)
    ): FilePickerConfig {
        this.selectAllText = selectAllString
        this.deSelectAllText = unSelectAllString
        this.hadSelectedText = hadSelectedStrRes
        this.confirmText = confirmText
        this.maxSelectCountTips = maxSelectCountTipsStrRes
        this.emptyListTips = emptyListTips
        return this
    }

    fun imageEngine(ie: ImageEngine): FilePickerConfig {
        this.customImageEngine = ie
        return this
    }

    /**
     * Pass your custom [FileType] instances list and all done! This lib would auto detect file type
     * by using [FileType.verify].
     * If [autoFilter] is true, this lib will filter result by using your custom file types.
     * If [autoFilter] is true, the library will automatically filter out files that do not meet your custom type.
     * Will not show up in the results. * If it is false, then only the detection type. No changes to the result list
     * You don't need to call [fileType] again !
     */
    fun registerFileType(types: List<FileType>, autoFilter: Boolean = true): FilePickerConfig {
        this.customFileTypes.addAll(types)
        this.defaultFileDetector.registerCustomTypes(customFileTypes)
        this.isAutoFilter = autoFilter
        return this
    }

    /**
     * Allow the use of thread pools in your project
     * The custom thread pool will not be closed by default,
     * if you need to close when the file selection is finished, please pass true
     */
    fun threadPool(threadPool: ExecutorService, autoShutdown: Boolean): FilePickerConfig {
        this.threadPool = threadPool
        this.threadPoolAutoShutDown = autoShutdown
        return this
    }

    fun forResult(activityResultLauncher: ActivityResultLauncher<Intent>) {
        val activity = pickerManager.contextRef?.get()
        val fragment = pickerManager.fragmentRef?.get()

        val intent = Intent(activity, FilePickerActivity::class.java)
        if (fragment == null) {
            activityResultLauncher.launch(intent)
        } else {
            activityResultLauncher.launch(intent)
        }
    }

    fun resetCustomFile() {
        this.customRootPathFile = null
    }

    fun clear() {
        this.customFileTypes.clear()
        this.customImageEngine = null
        this.selfFilter = null
        this.defaultFileDetector.clear()
        resetCustomFile()
    }

    companion object {
        @get:StorageMediaType
        const val STORAGE_EXTERNAL_STORAGE = "STORAGE_EXTERNAL_STORAGE"

        @get:StorageMediaType
        const val STORAGE_UUID_SD_CARD = "STORAGE_UUID_SD_CARD"

        @get:StorageMediaType
        const val STORAGE_UUID_USB_DRIVE = "STORAGE_UUID_USB_DRIVE"

        @get:StorageMediaType
        const val STORAGE_CUSTOM_ROOT_PATH = "STORAGE_CUSTOM_ROOT_PATH"

        @Retention(AnnotationRetention.SOURCE)
        annotation class StorageMediaType
    }

}