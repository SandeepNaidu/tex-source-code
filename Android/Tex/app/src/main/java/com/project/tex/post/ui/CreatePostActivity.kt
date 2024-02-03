package com.project.tex.post.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.project.tex.R
import com.project.tex.addmedia.FileUtils
import com.project.tex.base.activity.BaseActivity
import com.project.tex.calendar.CalendarActivity
import com.project.tex.databinding.*
import com.project.tex.db.table.LocalPost
import com.project.tex.main.ui.avid.avidCamera.view.NormalProgressDialog
import com.project.tex.main.ui.home.PostTypes
import com.project.tex.main.ui.player.MediaObserver
import com.project.tex.post.CreatePostViewModel
import com.project.tex.post.adapter.OptionAddAdapter
import com.project.tex.post.adapter.ThumbnailAdapter
import com.project.tex.post.dialog.AddCaptionDialog
import com.project.tex.utils.RangeExts.toStringArray
import com.project.tex.utils.ViewUtils
import com.project.tex.utils.pdf.PdfUtils
import com.steelkiwi.cropiwa.CropIwaView
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig
import com.steelkiwi.cropiwa.config.InitialPosition
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver
import com.steelkiwi.cropiwa.shape.CropIwaRectShape
import com.tbruyelle.rxpermissions3.RxPermissions
import com.video.editor.interfaces.OnCommandVideoListener
import com.video.editor.interfaces.OnVideoListener
import io.reactivex.android.schedulers.AndroidSchedulers
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.util.*
import kotlin.math.absoluteValue


class CreatePostActivity : BaseActivity<ActivityCreatePostBinding, CreatePostViewModel>(),
    View.OnClickListener, CropIwaResultReceiver.Listener, AddCaptionDialog.CaptionAddedCallback,
    ThumbnailAdapter.ClickListener {
    override fun getViewBinding() = ActivityCreatePostBinding.inflate(layoutInflater)

    override fun getViewModelInstance() =
        ViewModelProvider(this).get(CreatePostViewModel::class.java)

    private lateinit var img: ImageView
    private lateinit var listener: LocationListener
    private var pollDuration: Int = 7
    private var locationAddress: String? = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var isNewMusicPicked: Boolean = false
    private var videoTrimLayout: VideoPreviewLayoutBinding? = null
    private var seekPosition: Int = 0
    private val TAG: String = CreatePostActivity::class.java.simpleName
    private val PERMISSION_CODE = 1001
    private var lastCaption = ""
    private var hashTags = ""
    private var latLong = ""

    private var fileUri: Uri? = null
    private var audiofileUri: Uri? = null
    private var eventBannerfileUri: Uri? = null
    private var thumbFileBmp: Bitmap? = null
    private val postTypeLiveData = MutableLiveData("PHOTO")
    private var type: String? = null
    private var eventFormat: String = "External link"
    private var typeSelected = "Photo"
    var pdfUtils: PdfUtils? = null
    var audioContentLayoutBinding: AudioPlayLayoutBinding? = null
    private val eventLayout by lazy { EventPostTypeLayoutBinding.inflate(LayoutInflater.from(this)) }
    private val pollLayout by lazy { PollPostTypeLayoutBinding.inflate(LayoutInflater.from(this)) }
    private val thumnailAdapter by lazy { ThumbnailAdapter(mutableListOf()) }
    private var observer: MediaObserver? = null

    private var mediaPlayer: MediaPlayer? = null
    private val c = Calendar.getInstance()
    var permissionsList: ArrayList<String> = arrayListOf()
    var permissionsCount = 0
    var permissionAskedTime = 0

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Required!")
        builder.setMessage("Please provide access to Storage!")
        builder.setPositiveButton(
            "Allow",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    (this as BaseActivity<*, *>).checkStoragePermission(
                        Manifest.permission.RECORD_AUDIO,
                        PERMISSION_CODE
                    )
                } else {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri =
                        Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            })
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private var permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsList = ArrayList()
            permissionsCount = 0
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else if (!hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsCount++
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                    permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES)
                } else if (!hasPermission(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    permissionsCount++
                }
            }
            if (permissionsList.size > 0) {
                //Some permissions are denied and can be asked again.
                if (permissionAskedTime >= 1) {
                    permissionAskedTime = 0
                    return@registerForActivityResult
                }
                askForPermissions(permissionsList)
                permissionAskedTime++
            } else if (permissionsCount > 0) {
                /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
                showPermissionDialog()
            } else {
                //All permissions granted. Do your stuff 🤞
                when (typeSelected) {
                    "Photo" -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        activityResultImages.launch(
                            intent
                        )
                    }
                    "Document" -> {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "application/pdf"
                        activityResultDocument.launch(
                            intent
                        )
                    }
                }
            }
        }

    private var permissionsLauncherVideo =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsList = ArrayList()
            permissionsCount = 0
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else if (!hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsCount++
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO)) {
                    permissionsList.add(Manifest.permission.READ_MEDIA_VIDEO)
                } else if (!hasPermission(this, Manifest.permission.READ_MEDIA_VIDEO)) {
                    permissionsCount++
                }
            }
            if (permissionsList.size > 0) {
                //Some permissions are denied and can be asked again.
                if (permissionAskedTime >= 1) {
                    permissionAskedTime = 0
                    return@registerForActivityResult
                }
                askForPermissions(permissionsList)
                permissionAskedTime++
            } else if (permissionsCount > 0) {
                /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
                showPermissionDialog()
            } else {
                //All permissions granted. Do your stuff 🤞
                when (typeSelected) {
                    "Video" -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "video/*"
                        activityResultVideo.launch(
                            intent
                        )
                    }
                }
            }
        }

    private var permissionsLauncherAudio =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsList = ArrayList()
            permissionsCount = 0
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else if (!hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsCount++
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO)) {
                    permissionsList.add(Manifest.permission.READ_MEDIA_AUDIO)
                } else if (!hasPermission(this, Manifest.permission.READ_MEDIA_AUDIO)) {
                    permissionsCount++
                }
            }
            if (permissionsList.size > 0) {
                //Some permissions are denied and can be asked again.
                if (permissionAskedTime >= 1) {
                    permissionAskedTime = 0
                    return@registerForActivityResult
                }
                askForPermissions(permissionsList)
                permissionAskedTime++
            } else if (permissionsCount > 0) {
                /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
                showPermissionDialog()
            } else {
                //All permissions granted. Do your stuff 🤞
                when (typeSelected) {
                    "Music" -> {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "audio/*"
                        activityResultAudio.launch(
                            intent
                        )
                    }
                }
            }
        }

    private fun askForPermissions(permissionsList: ArrayList<String>) {
        val newPermissionStr = Array(permissionsList.size) {
            permissionsList[it]
        }

        if (newPermissionStr.isNotEmpty()) {
            when (typeSelected) {
                "Video" -> {
                    permissionsLauncherVideo.launch(newPermissionStr)
                }
                "Photo" -> {
                    permissionsLauncher.launch(newPermissionStr)
                }
                "Music" -> {
                    permissionsLauncherAudio.launch(newPermissionStr)
                }
            }
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showPermissionDialog()
        }
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    //cropper view
    private var cropView: CropIwaView? = null
    var resultReceiver: CropIwaResultReceiver? = null
    private val days = (1..90).toStringArray().toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding.backBtn.setOnClickListener(this)
        postTypeLiveData.value = intent?.getStringExtra("type") ?: "PHOTO"
        val types = resources.getStringArray(R.array.post_type)
        msg.updateSnackbarView(_binding.root)
        img = ImageView(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            RxPermissions(this).request(
                Manifest.permission.ACCESS_FINE_LOCATION
            ).subscribe {
                if (it) {
                    getCurrentLocation()
                }
            }
        } else {
            getCurrentLocation()
        }
        setPreviewContainer(postTypeLiveData.value!!)
        val index = types.indexOfFirst {
            StringUtils.equalsIgnoreCase(it, postTypeLiveData.value)
        }
        val adapterSpinner = ArrayAdapter.createFromResource(
            this, R.array.post_type, R.layout.simple_spinner_item
        )
        adapterSpinner.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_a)
        _binding.spinner.setAdapter(adapterSpinner)
        _binding.spinner.setSelection(index)
        _binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val prevPostType = typeSelected
                typeSelected = types[position]
                fileUri = null
                updatePagePreview()
                resetPreviousUIState(prevPostType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        _binding.recyclerView2.adapter = thumnailAdapter
        thumnailAdapter.setListener(this)
        setSpinnerAdapter(R.array.preview)
        observer()
        _binding.btnGallery.setOnClickListener(this)
        _binding.btnNext.setOnClickListener(this)
    }

    private fun setSpinnerAdapter(preview: Int) {
        val list = resources.getStringArray(preview)
        val adapterSpinner2 = ArrayAdapter.createFromResource(
            this, preview, R.layout.simple_spinner_item
        )
        adapterSpinner2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_a)
        _binding.recentSpinner.setAdapter(adapterSpinner2)
        _binding.recentSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (list.get(position) == "Gallery" && list.size > 1) {
                        _binding.btnGallery.callOnClick()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                latLong = "${it.result.latitude},${it.result.longitude}"
                viewModel.fetchLocation(it.result, this).observe(this) {
                    locationAddress = it
                }
            } else {
                val request = LocationRequest()
                if (!::listener.isInitialized) {
                    listener = LocationListener {
                        latLong = "${it.latitude},${it.longitude}"
                        viewModel.fetchLocation(it, this).observe(this) {
                            locationAddress = it
                            fusedLocationClient.removeLocationUpdates(listener)
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(
                    request, listener, Looper.getMainLooper()
                )
            }
        }
    }

    private fun resetPreviousUIState(prevPostType: String) {
        when (prevPostType) {
            "Music" -> {
                resetMediaPlayer()
            }
        }
    }

    private fun updatePagePreview() {
        _binding.previewContainer.removeAllViews()
        when (typeSelected) {
            "Photo" -> {
                setPreviewContainer(PostTypes.TYPE_PHOTO)
                (_binding.previewContainer.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "34:22"
            }
            "Video" -> {
                setPreviewContainer(PostTypes.TYPE_VIDEO)
                (_binding.previewContainer.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "34:22"
            }
            "Document" -> {
                setPreviewContainer(PostTypes.TYPE_DOCUMENT)
                (_binding.previewContainer.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "34:31"
            }
            "Music" -> {
                setPreviewContainer(PostTypes.TYPE_MUSIC)
                (_binding.previewContainer.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "34:15"
            }
            "Event" -> {
                setPreviewContainer(PostTypes.TYPE_EVENT)
            }
            "Poll" -> {
                setPreviewContainer(PostTypes.TYPE_POLL)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                msg.showShortMsg("Storage permission given")
//            } else {
//                msg.showLongMsg("Storage permission is required")
//            }
        }
    }

    private fun setPreviewContainer(postType: String) {
        postTypeLiveData.value = postType
        when (postType) {
            PostTypes.TYPE_PHOTO, PostTypes.TYPE_VIDEO -> {
                showMediaUI(true)
                if (postType == PostTypes.TYPE_PHOTO) {
                    val view = CropIwaView(this)
                    cropView = view
                    _binding.previewContainer.addView(cropView)
//                    img.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey))
//                    img.scaleType = ImageView.ScaleType.FIT_CENTER
//                    Glide.with(this).load(R.drawable.ic_placeholder_media).into(img)
//                    _binding.previewContainer.addView(img)
//                    img.isVisible = true
                    loadPlaceholder(R.drawable.ic_placeholder_media, R.color.dark_grey)
                } else {
                    loadPlaceholder(R.drawable.ic_video_placeholder, R.color.video_bg)
                }
            }
            PostTypes.TYPE_DOCUMENT -> {
                showMediaUI(true)
                //frame layout is only required nothing has to be added
                loadPlaceholder(R.drawable.ic_file_placeholder_pdf, R.color.video_bg)
            }
            PostTypes.TYPE_MUSIC -> {
                showMediaUI(true)
                if (audioContentLayoutBinding == null) {
                    audioContentLayoutBinding =
                        AudioPlayLayoutBinding.inflate(LayoutInflater.from(this))
                }
                _binding.previewContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.audio_bg_color
                    )
                )
                Glide.with(this).load(R.drawable.ic_album_placeholder)
                    .into(audioContentLayoutBinding!!.audioThumbnailCv)
                _binding.previewContainer.addView(audioContentLayoutBinding!!.root)
            }
            PostTypes.TYPE_EVENT -> {
                showMediaUI(false)
                putEventUI()
            }
            PostTypes.TYPE_POLL -> {
                showMediaUI(false)
                putPollUI()
            }
        }
    }

    private fun loadPlaceholder(icVideoPlaceholder: Int, bgColor: Int) {
        img.setBackgroundColor(ContextCompat.getColor(this, bgColor))
        img.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(this).load(icVideoPlaceholder).into(img)
        _binding.previewContainer.addView(img)
        img.isVisible = true
    }

    private fun putPollUI() {
        if (_binding.formContainer.findViewById<View>(R.id.poll_event) == null) {
            if (_binding.formContainer.childCount > 0) {
                _binding.formContainer.removeAllViews()
            }
            _binding.formContainer.addView(pollLayout.root)
            if (pollLayout.optionRv.adapter == null) {
                pollLayout.optionRv.adapter = OptionAddAdapter(
                    mutableListOf(
                        OptionAddAdapter.OptionData("Option 1", ""),
                        OptionAddAdapter.OptionData("Option 2", "")
                    )
                )
            }
            pollLayout.addOptionBtn.setOnClickListener {
                (pollLayout.optionRv.adapter as OptionAddAdapter).addOption()
            }
            pollLayout.pollDurationTv.setOnClickListener {
                numberPickerCustom(
                    getString(R.string.select_duration), CalendarActivity.NumberPickerSelected {
                        pollDuration = it.toIntOrNull() ?: 1
                        pollLayout.pollDurationTv.text =
                            resources.getQuantityString(R.plurals.days, pollDuration, pollDuration)
                    }, 90, 1, days, pollDuration
                )
            }

            pollLayout.btnCreateEvent.setOnClickListener {
                val list = (pollLayout.optionRv.adapter as? OptionAddAdapter)?.getOptionData()
                val count = list?.stream()?.filter {
                    it.optionValue.isNotEmpty()
                }?.count()
                if (pollLayout.edtQuestion.text?.isEmpty() == true) {
                    msg.showShortMsg("Please enter a Poll question")
                    return@setOnClickListener
                } else if ((count ?: 0L) == 0L) {
                    msg.showShortMsg("Please enter the options")
                    return@setOnClickListener
                } else if ((count ?: 0L) < 2) {
                    msg.showShortMsg("Please enter atleast 2 options")
                    return@setOnClickListener
                }
                confirmNProcessFile()
            }
        }
    }

    private fun showMediaUI(b: Boolean) {
        _binding.groupBottomUi.isVisible = b
        _binding.formContainer.isVisible = !b
    }

    private fun observer() {
        postTypeLiveData.observe(this) { type ->
            viewModel.getAllTakes(type).observe(this) {
                thumnailAdapter.updateList(it)
                if (it.isEmpty()) {
                    setSpinnerAdapter(R.array.preview)
                    _binding.noTakes.isVisible =
                        !(typeSelected.equals(PostTypes.TYPE_POLL, true) || typeSelected.equals(
                            PostTypes.TYPE_EVENT,
                            true
                        ))
                    _binding.btnGallery.isVisible = true
                } else {
                    setSpinnerAdapter(R.array.preview_after)
                    _binding.noTakes.isVisible = false
                    _binding.btnGallery.isVisible = false
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_btn -> {
                videoTrimLayout?.videoTrimmer?.destroy()
                finish()
            }
            R.id.btn_gallery -> {
                pickFile()
            }
            R.id.btn_next -> {
                if (typeSelected.equals(PostTypes.TYPE_PHOTO, true)) {
                    if (fileUri == null) {
                        msg.showShortMsg("Please select an image to create Post!")
                        return
                    }
                } else if (typeSelected.equals(PostTypes.TYPE_VIDEO, true)) {
                    if (fileUri == null) {
                        msg.showShortMsg("Please select a video to create Post!")
                        return
                    }
                } else if (typeSelected.equals(PostTypes.TYPE_MUSIC, true)) {
                    if (fileUri == null) {
                        msg.showShortMsg("Please select a music to create Post!")
                        return
                    }
                } else if (typeSelected.equals(PostTypes.TYPE_DOCUMENT, true)) {
                    if (fileUri == null) {
                        msg.showShortMsg("Please select a document to create Post!")
                        return
                    }
                }
                openCaption()
            }
        }
    }

    private fun openCaption() {
        AddCaptionDialog().showDialog(supportFragmentManager, this)
    }

    private fun pickFile() {
        when (typeSelected) {
            "Photo" -> {
                pickImage()
            }
            "Video" -> {
                pickVideo()
            }
            "Music" -> {
                pickAudio()
            }
            "Document" -> {
                pickDocument()
            }
            "Event" -> {
                // no file is to be picked
            }
            "Poll" -> {
                // no file is to be picked
            }
        }
    }

    private fun putEventUI() {
        if (_binding.formContainer.findViewById<View>(R.id.post_event) == null) {
            if (_binding.formContainer.childCount > 0) {
                _binding.formContainer.removeAllViews()
            }
            _binding.formContainer.addView(eventLayout.root)
            val list = resources.getStringArray(R.array.event_format)
            val adapterSpinner = ArrayAdapter.createFromResource(
                this, R.array.event_format, R.layout.simple_spinner_item_for_event
            )
            adapterSpinner.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_a)
            eventLayout.eventFormatTv.setAdapter(adapterSpinner)
            eventLayout.eventFormatTv.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    eventFormat = list[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            eventLayout.tvEventBanner.setOnClickListener {
                pickEventImage()
            }

            eventLayout.btnCreateEvent.setOnClickListener {
                if (eventFormat.isEmpty()) {
                    msg.showShortMsg("Please select a event format")
                    return@setOnClickListener
                } else if (eventLayout.edtEventName.text?.isEmpty() == true) {
                    msg.showShortMsg("Please enter the event name")
                    return@setOnClickListener
                } else if (eventLayout.edtDatetime.text?.isEmpty() == true) {
                    msg.showShortMsg("Please select a date and time for the event.")
                    return@setOnClickListener
                } else if (eventLayout.extEventName.text?.isEmpty() == true) {
                    msg.showShortMsg("Please enter the event name")
                    return@setOnClickListener
                } else if (eventLayout.descName.text?.isEmpty() == true) {
                    msg.showShortMsg("Please enter the description")
                    return@setOnClickListener
                } else if (eventBannerfileUri == null) {
                    msg.showShortMsg("Please select the event banner")
                    return@setOnClickListener
                }
                confirmNProcessFile()
            }

            eventLayout.edtDatetime.setOnClickListener {
                // Get Current Date
                val datePickerDialog = DatePickerDialog(
                    this, { view, year, monthOfYear, dayOfMonth ->
                        eventLayout.edtDatetime.text = String.format(
                            "%02d/%02d/%02d at %02d:%02d", dayOfMonth, (monthOfYear + 1), year, 0, 0
                        )
//                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year + " at 00:00"
                        c[Calendar.YEAR] = year
                        c[Calendar.MONTH] = monthOfYear
                        c[Calendar.DAY_OF_MONTH] = dayOfMonth
                        // Launch Time Picker Dialog
                        val timePickerDialog = TimePickerDialog(
                            this, { view, hourOfDay, minute ->
                                eventLayout.edtDatetime.text = String.format(
                                    "%02d/%02d/%02d at %02d:%02d",
                                    dayOfMonth,
                                    (monthOfYear + 1),
                                    year,
                                    hourOfDay,
                                    minute
                                )
//                                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year + " at $hourOfDay:$minute"
                                c[Calendar.HOUR_OF_DAY] = hourOfDay
                                c[Calendar.MINUTE] = minute
                            }, c[Calendar.HOUR_OF_DAY], c[Calendar.MINUTE], true
                        )
                        timePickerDialog.show()
                    }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog.datePicker.minDate = c.timeInMillis
                datePickerDialog.show()
            }
        }
    }

    private fun numberPickerCustom(
        title: String,
        listener: CalendarActivity.NumberPickerSelected,
        max: Int,
        min: Int,
        list: Array<String>?,
        sel: Int
    ) {
        val d = AlertDialog.Builder(this)
        val dialogView = NumberPickerDialogBinding.inflate(layoutInflater)
        d.setView(dialogView.root)
        dialogView.title.text = title
        val alertDialog = d.create()

        val numberPicker = dialogView.dialogNumberPicker
        numberPicker.maxValue = (max - min).absoluteValue
        numberPicker.minValue = 0
        numberPicker.value = sel - 1
        list?.let {
            // for month we have defined values in strings.xml file
            numberPicker.displayedValues = list
        }

        dialogView.btnDone.setOnClickListener {
            listener.numberSelected(numberPicker.displayedValues.get(numberPicker.value))
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun confirmNProcessFile() {
        var i = 0
        val url: Array<String> = arrayOf("", "")
        when (typeSelected) {
            "Photo" -> {
                NormalProgressDialog.showLoading(
                    this, getString(R.string.media_file_uploading), false
                )
                val file = File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
                cropView?.crop(
                    CropIwaSaveConfig.Builder(Uri.fromFile(file))
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setQuality(80) //Hint for lossy compression formats
                        .build()
                )
                resultReceiver = CropIwaResultReceiver()
                resultReceiver?.setListener(this)
                resultReceiver?.register(this)
            }
            "Video" -> {
                NormalProgressDialog.showLoading(
                    this, getString(R.string.media_file_uploading), false
                )
                videoTrimLayout?.videoTrimmer?.save()
            }
            "Music" -> {
                NormalProgressDialog.showLoading(
                    this, getString(R.string.media_file_uploading), false
                )
                fileUri?.let {
                    viewModel.startUploadMusicPost(
                        it, audiofileUri
                    )!!.observeOn(AndroidSchedulers.mainThread())?.subscribe({ url[i++] = it }, {
                        NormalProgressDialog.stopLoading()
                        msg.showShortMsg(getString(R.string.failed_to_upload_file))
                        Log.e(TAG, "confirmNProcessFile: ", it)
                    }, {
                        viewModel.createMediaPost(
                            thumbImg = url[1],
                            postContentUrl = url[0],
                            postType = typeSelected,
                            postCaption = lastCaption,
                            hashTags = hashTags,
                            latLong = latLong,
                            address = locationAddress ?: ""
                        ).observe(this) { response ->
                            NormalProgressDialog.stopLoading()
                            Log.d(TAG, "confirmNProcessFile: $response")
                            if (response.responseCode == 200) {
                                msg.showShortMsg(getString(R.string.media_file_upload))
                                finish()
                            } else {
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            }
                        }
                    })
                } ?: kotlin.run {
                    NormalProgressDialog.stopLoading()
                    msg.showShortMsg(getString(R.string.failed_to_get_media_url))
                }
            }
            "Document" -> {
                NormalProgressDialog.showLoading(
                    this, getString(R.string.media_file_uploading), false
                )
                fileUri?.let {
                    thumbFileBmp?.let { it1 ->
                        viewModel.startUploadDocumentPost(
                            it, it1
                        )!!.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe({ url[i++] = it }, {
                                NormalProgressDialog.stopLoading()
                                msg.showShortMsg(getString(R.string.failed_to_upload_file))
                                Log.e(TAG, "confirmNProcessFile: ", it)
                            }, {
                                viewModel.createMediaPost(
                                    thumbImg = url[1],
                                    postContentUrl = url[0],
                                    postType = typeSelected,
                                    postCaption = lastCaption,
                                    hashTags = hashTags,
                                    latLong = latLong,
                                    address = locationAddress ?: ""
                                ).observe(this) { response ->
                                    NormalProgressDialog.stopLoading()
                                    Log.d(TAG, "confirmNProcessFile: $response")
                                    if (response.responseCode == 200) {
                                        msg.showShortMsg(getString(R.string.media_file_upload))
                                        finish()
                                    } else {
                                        msg.showShortMsg(getString(R.string.something_wrong_try_again))
                                    }
                                }
                            })
                    } ?: kotlin.run {
                        NormalProgressDialog.stopLoading()
                        msg.showShortMsg(getString(R.string.failed_to_getmedia))
                    }
                } ?: kotlin.run {
                    NormalProgressDialog.stopLoading()
                    msg.showShortMsg(getString(R.string.failed_to_get_media_url))
                }
            }
            "Event" -> {
                NormalProgressDialog.showLoading(
                    this, getString(R.string.media_file_uploading), false
                )
                eventBannerfileUri?.let {
                    viewModel.createEventPost(
                        eventType = if (eventLayout.inpersonType.isChecked) "Inperson" else "Online",
                        eventFormat = eventLayout.eventFormatTv.selectedItemPosition.toString(),
                        event = eventLayout.edtEventName.text.toString(),
                        eventTime = (c.timeInMillis / 1000).toString(),
                        eventExternalLink = eventLayout.extEventName.text.toString(),
                        description = eventLayout.descName.text.toString(),
                        latLong = latLong,
                        bannerUrl = it,
                        address = locationAddress ?: ""
                    ).observe(this) { response ->
                        NormalProgressDialog.stopLoading()
                        Log.d(TAG, "confirmNProcessFile: $response")
                        if (response.responseCode == 200) {
                            msg.showShortMsg("Event Created")
                            finish()
                        } else {
                            msg.showShortMsg(getString(R.string.something_wrong_try_again))
                        }
                    }
                } ?: kotlin.run {
                    msg.showShortMsg("Event banner image not selected")
                }
            }
            "Poll" -> {
                val list = (pollLayout.optionRv.adapter as? OptionAddAdapter)?.getOptionData()
                val valueList = mutableListOf<String>()
                list?.forEachIndexed { index, optionData ->
                    valueList.add(optionData.optionValue)
                }
                val s = valueList.toTypedArray()
                viewModel.createPollPost(
                    question = pollLayout.edtQuestion.text.toString(),
                    options = s,
                    duration = pollDuration,
                    latLong = latLong,
                    address = locationAddress ?: ""
                ).observe(this) { response ->
                    Log.d(TAG, "confirmNProcessFile: $response")
                    if (response.responseCode == 200) {
                        msg.showShortMsg("Poll Created")
                        finish()
                    } else {
                        msg.showShortMsg(getString(R.string.something_wrong_try_again))
                    }
                }
            }
        }
    }

    private val activityResultVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                fileUri = Objects.requireNonNull<Intent>(result.data).data
                loadVideoFromStorage()
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(applicationContext, fileUri)
                val path = fileUri?.let { FileUtils.getPath(this@CreatePostActivity, it) }
                if (path != null) {
                    thumbFileBmp = retriever.getFrameAtTime(2000)
                    viewModel.addPostToDb(
                        path, thumbFileBmp, UUID.randomUUID().toString(), PostTypes.TYPE_VIDEO
                    )
                }
                retriever.release()
            }
        }

    private fun loadVideoFromStorage() {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(applicationContext, fileUri)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMilli = time!!.toLong()
        retriever.release()
        if (timeInMilli > 600000) {
            msg.showLongMsg("Video must be of 10 minutes or less")
        } else {
            img.isVisible = false
            type = "video"
            videoTrimLayout = VideoPreviewLayoutBinding.inflate(LayoutInflater.from(this))
            val outputDirectory = cacheDir.toString() + File.separator + "TrimCrop" + File.separator
            videoTrimLayout!!.videoTrimmer.setOnCommandListener(object : OnCommandVideoListener {
                override fun onStarted() {
                    Log.d(TAG, "onStarted: ")
                }

                override fun getResult(uri: Uri) {
                    Log.d(TAG, "getResult: $uri")
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(applicationContext, uri)
                    thumbFileBmp = retriever.getFrameAtTime(2000)
                    thumbFileBmp?.let {
                        var i = 0
                        val url: Array<String> = arrayOf("", "")
                        viewModel.startUploadPostData(uri, it)!!
                            .observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe({ url[i++] = it }, {
                                NormalProgressDialog.stopLoading()
                                msg.showShortMsg(getString(R.string.failed_to_upload_file))
                                Log.e(TAG, "onCropSuccess: ", it)
                            }, {
                                viewModel.createMediaPost(
                                    thumbImg = url[1],
                                    postContentUrl = url[0],
                                    postType = typeSelected,
                                    postCaption = lastCaption,
                                    hashTags = hashTags,
                                    latLong = latLong,
                                    address = locationAddress ?: ""
                                ).observe(this@CreatePostActivity) { response ->
                                    NormalProgressDialog.stopLoading()
                                    Log.d(TAG, "onCropSuccess: $response")
                                    if (response.responseCode == 200) {
                                        msg.showShortMsg(getString(R.string.media_file_upload))
                                        finish()
                                    } else {
                                        msg.showShortMsg(getString(R.string.something_wrong_try_again))
                                    }
                                }
                            })
                    } ?: kotlin.run {
                        NormalProgressDialog.stopLoading()
                        msg.showShortMsg(getString(R.string.failed_to_getmedia))
                    }
                }

                override fun cancelAction() {
                    NormalProgressDialog.stopLoading()
                }

                override fun onError(message: String) {
                    NormalProgressDialog.stopLoading()
                    msg.showShortMsg(getString(R.string.msg_failed_video_process))
                }
            }).setOnVideoListener(object : OnVideoListener {
                override fun onVideoPrepared() {
                    Log.d(TAG, "onVideoPrepared: ")
                }
            }).setVideoURI(fileUri!!).setVideoInformationVisibility(true).setMaxDuration(600)
                .setMinDuration(5).setDestinationPath(
                    outputDirectory
                )
            _binding.previewContainer.addView(videoTrimLayout!!.root)
        }
    }

    private val activityResultAudio =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                val uri = Objects.requireNonNull<Intent>(result.data).data
                isNewMusicPicked = true
                loadAudioFromStorage(uri)
                addPostToDb(null)
            }
        }

    private val activityResultAudioThumbPick =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                val uri = Objects.requireNonNull<Intent>(result.data).data
                val intent2 = Intent(this, CropActivity::class.java)
                intent2.putExtra("type", "Audio")
                intent2.putExtra("uri", uri)
                activityResultCropAudioThumbPick.launch(intent2)
            }
        }

    private val activityResultEventImagePick =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                val uri = Objects.requireNonNull<Intent>(result.data).data
                val intent2 = Intent(this, CropActivity::class.java)
                intent2.putExtra("type", "event")
                intent2.putExtra("uri", uri)
                activityResultCropEventImagePick.launch(intent2)
            }
        }


    private val activityResultCropAudioThumbPick =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                val uri = Objects.requireNonNull<Intent>(result.data).data
                audiofileUri = uri
                audioContentLayoutBinding?.audioThumbnailCv?.let { it1 ->
                    Glide.with(this).load(audiofileUri).into(
                        it1
                    )
                }
            }
        }

    private val activityResultCropEventImagePick =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                val uri = Objects.requireNonNull<Intent>(result.data).data
                eventBannerfileUri = uri
                eventLayout.ivEventBanner.let { it1 ->
                    Glide.with(this).load(eventBannerfileUri).into(
                        it1
                    )
                }
                eventLayout.ivEventBanner.isVisible = true
            }
        }

    private fun loadAudioFromStorage(
        result: Uri?
    ) {
        fileUri = result
        type = "audio"
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA).build()
                )
            }
        } else {
            resetMediaPlayer()
        }

        audioContentLayoutBinding?.apply {
            progressBar.progressDrawable.mutate()
            progressBar.progress = seekPosition
            progressBar.max = mediaPlayer?.duration ?: 0
            playerControl.setOnClickListener {
                playerControl.isChecked = !playerControl.isChecked
                if (playerControl.isChecked) {
                    playerWaves.playAnimation()
                    loadAudio()
                    mediaPlayer?.start()
                    runMedia(mediaPlayer!!, progressBar)
                } else {
                    playerWaves.pauseAnimation()
                    playerWaves.frame = 0
                    mediaPlayer?.pause()
                    seekPosition = mediaPlayer?.currentPosition ?: 0
                    observer?.stop()
                }
                mediaPlayer?.setOnCompletionListener {
                    observer?.stop()
                    playerWaves.pauseAnimation()
                    playerWaves.frame = 0
                    playerControl.isChecked = false
                    progressBar.progress = it.currentPosition
                }
            }
            audioThumbnailCv.setOnClickListener {
                pickAudioImage()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        observer?.stop()
    }

    fun runMedia(mPlayer: MediaPlayer, progress: ProgressBar) {
        observer = MediaObserver(mPlayer, progress)
        Thread(observer).start()
    }

    private val activityResultDocument =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            fileUri = it.data?.data
            type = "document"
            loadDocumentFromStorage()
        }

    private fun loadDocumentFromStorage() {
        fileUri?.let {
            img.isVisible = false
            pdfUtils = FileUtils.getPath(this@CreatePostActivity, fileUri!!)
                ?.let { it1 -> PdfUtils(this, filePath = it1) }
            loadDocument()
        }
    }

    private fun loadDocument() {
        pdfUtils?.loadFile(object : PdfUtils.PDFListener {
            override fun loaded() {
                Log.d(TAG, "loaded: ")
            }

            override fun fail(e: Throwable) {
                Log.e(TAG, "fail: ", e)
            }

            override fun complete() {
                Log.d(TAG, "complete: ")
                val surveyContent =
                    FragmentPdfRendererBinding.inflate(LayoutInflater.from(this@CreatePostActivity))
                pdfUtils?.renderAllPages(object : PdfUtils.PageReadyListener {
                    override fun onPagesReady(list: List<Bitmap>) {
                        Log.d(
                            TAG, "onPagesReady: called with items count : " + list.size
                        )
                        if (surveyContent.pdfContainer.childCount > 0) return
                        for (bmp in list) {
                            val page = PagePdfRendererBinding.inflate(
                                LayoutInflater.from(
                                    this@CreatePostActivity
                                )
                            )

                            surveyContent.pdfContainer.addView(page.root)
                            page.images.setImageBitmap(bmp)
                            val params = page.root.layoutParams as ViewGroup.MarginLayoutParams
                            val margin = ViewUtils.dpToPx(this@CreatePostActivity, 16)
                            params.setMargins(margin, margin, margin, margin)
                            page.root.layoutParams = params
                        }
                        if (list.isNotEmpty()) {
                            val filePath = fileUri?.let {
                                FileUtils.getPath(
                                    this@CreatePostActivity, it
                                )
                            }
                            if (filePath != null) {
                                viewModel.addPostToDb(
                                    filePath,
                                    list[0],
                                    UUID.randomUUID().toString(),
                                    PostTypes.TYPE_DOCUMENT
                                )
                            }
                            thumbFileBmp = ThumbnailUtils.extractThumbnail(
                                list[0], 300, 480
                            )
                        }
                    }

                    override fun fail(e: Throwable) {
                        Log.e(TAG, "fail: ", e)
                    }

                    override fun complete() {
                        Log.d(TAG, "complete: called")
                        _binding.previewContainer.setBackgroundColor(
                            ContextCompat.getColor(
                                this@CreatePostActivity, R.color.drawable_outline_grey
                            )
                        )
                        _binding.previewContainer.addView(surveyContent.root)
                    }
                })
            }
        })
    }

    private fun loadAudio() {
        if (isNewMusicPicked) {
            mediaPlayer?.setDataSource(fileUri?.let { FileUtils.getReadablePathFromUri(this, it) })
            mediaPlayer?.prepare()
            isNewMusicPicked = false
            audioContentLayoutBinding?.progressBar?.max = mediaPlayer?.duration ?: 0
        }
    }

    private fun resetMediaPlayer() {
        mediaPlayer?.pause()
        mediaPlayer?.reset()
        isNewMusicPicked = false
    }

    private val activityResultImages =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                val uri = Objects.requireNonNull<Intent>(result.data).data
                loadImageFromStorage(uri!!)
                addPostToDb(null)
            }
        }

    private fun loadImageFromStorage(fileU: Uri) {
        fileUri = fileU
        type = "image"
        img.isVisible = false
        cropView?.setImageUri(fileUri)
        cropView?.apply {
            configureOverlay().also {
                it.isDynamicCrop = false
//                it.aspectRatio = AspectRatio(width, height)
                it.cropShape = CropIwaRectShape(it)
                it.setShouldDrawGrid(false)
                it.overlayColor = Color.TRANSPARENT
                it.minWidth = width
                it.minHeight = height
            }.apply()
            configureImage().apply {
                imageInitialPosition = InitialPosition.CENTER_CROP
                isImageScaleEnabled = true
                isImageTranslationEnabled = true
                maxScale = 2f
                minScale = 0.25f
            }.apply()
        }
    }

    private fun addPostToDb(thumbUrl: String?) {
        val filePath = fileUri?.let { FileUtils.getPath(this, it) }
        if (filePath != null) {
            viewModel.addPostToDb(
                filePath, thumbUrl ?: filePath, UUID.randomUUID().toString(), PostTypes.TYPE_PHOTO
            )
        }
    }

    override fun onDestroy() {
        resultReceiver?.unregister(this)
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun pickImage() {
        permissionsLauncher.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                ) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    }

    private fun pickAudioImage() {
        if (checkStoragePermission(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_IMAGES
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE
            )
        ) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra("type", "Audio")
            activityResultAudioThumbPick.launch(
                intent
            )
        } else {
            msg.showIndefinteMsg(
                "Please provide access to Image files!", "Allow"
            ) {
                checkStoragePermission(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        Manifest.permission.READ_MEDIA_IMAGES
                    else
                        Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE
                )
            }
        }
    }

    private fun pickEventImage() {
        if (checkStoragePermission(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_IMAGES
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE
            )
        ) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra("type", "event")
            activityResultEventImagePick.launch(
                intent
            )
        } else {
            showPermissionDialog()
        }
    }

    private fun pickVideo() {
        permissionsLauncherVideo.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                ) else
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    }

    private fun pickAudio() {
        permissionsLauncherAudio.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                ) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    }

    private fun pickDocument() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "application/pdf"
            activityResultDocument.launch(
                intent
            )
        }
    }

    override fun onCropSuccess(croppedUri: Uri?) {
        var i = 0
        val url: Array<String> = arrayOf("", "")
        if (croppedUri != null) {
            thumbFileBmp = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(croppedUri.path), 256, 256
            )
            thumbFileBmp?.let {
                viewModel.startUploadImagePostData(croppedUri, it)!!
                    .observeOn(AndroidSchedulers.mainThread())?.subscribe({ url[i++] = it }, {
                        NormalProgressDialog.stopLoading()
                        msg.showShortMsg(getString(R.string.failed_to_upload_file))
                        Log.e(TAG, "onCropSuccess: ", it)
                    }, {
                        viewModel.createMediaPost(
                            thumbImg = url[1],
                            postContentUrl = url[0],
                            postType = typeSelected,
                            postCaption = lastCaption,
                            hashTags = hashTags,
                            latLong = latLong,
                            address = locationAddress ?: ""
                        ).observe(this) { response ->
                            NormalProgressDialog.stopLoading()
                            Log.d(TAG, "onCropSuccess: $response")
                            if (response.responseCode == 200) {
                                msg.showShortMsg(getString(R.string.media_file_upload))
                                finish()
                            } else {
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            }
                        }
                    })
            } ?: kotlin.run {
                NormalProgressDialog.stopLoading()
                msg.showShortMsg(getString(R.string.failed_to_getmedia))
            }
        } else {
            NormalProgressDialog.stopLoading()
            msg.showShortMsg("Failed to get cropped image!")
        }
    }

    override fun onCropFailed(e: Throwable?) {
        NormalProgressDialog.stopLoading()
        Log.e(TAG, "onCropFailed: ", e)
    }

    override fun onCaptionAdded(caption: String, joinToString: String) {
        Log.d(TAG, "onCaptionAdded: $caption")
        lastCaption = caption
        hashTags = joinToString
        confirmNProcessFile()
    }

    override fun onClick(post: LocalPost) {
        when (post.postType) {
            PostTypes.TYPE_PHOTO -> {
                fileUri = Uri.fromFile(post.postLocalUri?.let {
                    if (!File(it).exists()) {
                        msg.showShortMsg("File does not exist anymore")
                        viewModel.deleteFileFromDB(post.postId)
                        return
                    }
                    File(it)
                })
                loadImageFromStorage(fileUri!!)
            }
            PostTypes.TYPE_VIDEO -> {
                fileUri = Uri.fromFile(post.postLocalUri?.let {
                    if (!File(it).exists()) {
                        msg.showShortMsg("File does not exist anymore")
                        viewModel.deleteFileFromDB(post.postId)
                        return
                    }
                    File(it)
                })
                loadVideoFromStorage()
            }
            PostTypes.TYPE_DOCUMENT -> {
                fileUri = Uri.fromFile(post.postLocalUri?.let {
                    if (!File(it).exists()) {
                        msg.showShortMsg("File does not exist anymore")
                        viewModel.deleteFileFromDB(post.postId)
                        return
                    }
                    File(it)
                })
                loadDocumentFromStorage()
            }
            PostTypes.TYPE_MUSIC -> {
                fileUri = Uri.fromFile(post.postLocalUri?.let {
                    if (!File(it).exists()) {
                        msg.showShortMsg("File does not exist anymore")
                        viewModel.deleteFileFromDB(post.postId)
                        return
                    }
                    File(it)
                })
                isNewMusicPicked = true
                loadAudioFromStorage(fileUri)
            }
            PostTypes.TYPE_EVENT -> {
                // nothing to be done here
            }
            PostTypes.TYPE_POLL -> {
                // nothing to be done here
            }
        }
    }
}