package com.project.tex.aritist_profile.ui

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.project.tex.R
import com.project.tex.addmedia.FileUtils
import com.project.tex.aritist_profile.adapter.ImageSliderAdapter
import com.project.tex.aritist_profile.adapter.ViewPagerAdapter
import com.project.tex.aritist_profile.dialog.AddMediaDialog
import com.project.tex.aritist_profile.dialog.ProfileActionDialog
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityProfileBinding
import com.project.tex.main.ui.avid.avidCamera.view.NormalProgressDialog
import com.project.tex.post.ui.CreatePostActivity
import com.project.tex.utils.IntentUtils
import com.project.tex.utils.pdf.PdfUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.StringUtils
import java.util.*

class ProfileActivity : BaseActivity<ActivityProfileBinding, ProfileViewModel>(),
    View.OnClickListener, ProfileActionDialog.ClickAction, AddMediaDialog.ClickAction {

    private val TAG: String = ProfileActivity::class.java.simpleName
    private var thumbFileBmp: Bitmap? = null
    private val PERMISSION_CODE: Int = 12345
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var imagePagerAdapter: ImageSliderAdapter

    private var id = -1
    private var like = 0
    private var share = 0
    private var save = 0

    override fun getViewBinding() = ActivityProfileBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this)[ProfileViewModel::class.java]
    private var fileUri: Uri? = null
    private var type: String = "image"
    var pdfUtils: PdfUtils? = null
    private val listOFCoverImages = mutableListOf<CoverImage>()
    var permissionsList: ArrayList<String> = arrayListOf()
    var permissionsCount = 0
    var permissionAskedTime = 0

    private val profileActionDialog by lazy {
        ProfileActionDialog()
    }

    private val addMediaDialog by lazy {
        AddMediaDialog()
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
                when (type) {
                    "image" -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        activityResultImages.launch(
                            intent
                        )
                    }
                    "document" -> {
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
                when (type) {
                    "video" -> {
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
                when (type) {
                    "audio" -> {
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
            when (type) {
                "video" -> {
                    permissionsLauncherVideo.launch(newPermissionStr)
                }
                "image" -> {
                    permissionsLauncher.launch(newPermissionStr)
                }
                "audio" -> {
                    permissionsLauncherAudio.launch(newPermissionStr)
                }
            }
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showPermissionDialog()
        }
    }

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

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val activityResultProfileUpdate1 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                viewModel.getUpdateProfileData(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.toolbar)

        id = intent.getIntExtra("id", -1)

        viewModel.setUserId(id)
        if (viewModel.getIsSelfView()) {
            _binding.content.fabDots.isVisible = true
            _binding.content.likeCtv.isEnabled = false
            _binding.content.shareCtv.isEnabled = false
            _binding.content.saveCtv.isEnabled = false
        } else {
            _binding.content.fabDots.isVisible = false
            _binding.content.likeCtv.setOnClickListener(this)
            _binding.content.shareCtv.setOnClickListener(this)
            _binding.content.saveCtv.setOnClickListener(this)
            viewModel.viewProfile()
        }

        val listOfFragment = listOf(
            PortfolioFragment.newInstance(),
            ExperienceFragment.newInstance(),
            PostFragment.newInstance()
        )
        imagePagerAdapter = ImageSliderAdapter(this, listOFCoverImages)
        _binding.content.profileCoverPager.adapter = imagePagerAdapter
//        _binding.content.pageIndicatorView.setViewPager(_binding.content.profileCoverPager)
        viewPagerAdapter = ViewPagerAdapter(this, listOfFragment)
        _binding.content.gridContentViewpager.adapter = viewPagerAdapter

        _binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        TabLayoutMediator(
            _binding.content.tabs,
            _binding.content.gridContentViewpager
        ) { tab, position ->
            tab.text = when (position) {
                0 -> {
                    getString(R.string.portfolio)
                }
                1 -> {
                    getString(R.string.experience)
                }
                2 -> {
                    getString(R.string.posts)
                }
                else -> {
                    ""
                }
            }
        }.attach()

        observers()

        _binding.content.fabDots.setOnClickListener(this)
    }

    private fun observers() {
        viewModel.userNameData.observe(this, Observer {
            _binding.content.usernameTitle.text = it
        })
        viewModel.portfolioLd.observe(this, Observer { portfolio ->
            if (portfolio.responseCode == 200 && portfolio.body?.portfolios != null) {
                listOFCoverImages.clear()
                portfolio.body.portfolios.data?.forEach { d ->
                    if (d != null && StringUtils.isNotEmpty(d.imageUrl)) {
                        if (listOFCoverImages.size < 2) {
                            d.imageUrl?.let { listOFCoverImages.add(CoverImage(0, it)) }
                        } else {
                            return@forEach
                        }
                    }
                }
            }

            viewModel.getUpdateProfileData(this)
        })

        viewModel.profileDataLd.observe(this, Observer {
            it?.let { user ->
                _binding.content.usernameTitle.text =
                    ((user.firstName ?: "") + " " + (user.lastName ?: "")).removePrefix(" ").trim()
                _binding.content.locationTv.text = user.location ?: ""
                _binding.content.aboutContent.text =
                    user.bio ?: "Bio not added"
                _binding.content.genderValue.text = user.gender ?: "NA"
                _binding.content.ageValue.text = user.age ?: "NA"
                _binding.content.heightValue.text =
                    if (user.height != null) user.height + " cms" else "NA"
                user.profileImage?.let { it1 ->
                    if (listOFCoverImages.find { i -> i.imageType == 1 } != null) {
                        val index = listOFCoverImages.indexOfFirst { i -> i.imageType == 1 }
                        if (index != -1) {
                            listOFCoverImages.removeAt(index)
                            listOFCoverImages.add(
                                index,
                                CoverImage(1, it1)
                            )
                        } else {
                            listOFCoverImages.add(
                                0,
                                CoverImage(1, it1)
                            )
                        }
                    } else {
                        listOFCoverImages.add(
                            0,
                            CoverImage(1, it1)
                        )
                    }
                }
                imagePagerAdapter.notifyDataSetChanged()
                viewModel.saveUserData(user)
            }
        })

        viewModel.add(
            viewModel.getProfileActions().subscribe(
                {
                    if (it.responseCode == 200 && it.body?.profile != null && it.body.profile.isNotEmpty()) {
                        like = (it.body.profile.get(0)?.likeCount ?: 0)
                        share = (it.body.profile.get(0)?.shareCount ?: 0)
                        save = (it.body.profile.get(0)?.saveCount ?: 0)

                        _binding.content.likeCtv.text = "$like likes"
                        _binding.content.shareCtv.text = "$share Shares"
                        _binding.content.saveCtv.text = "$save saves"
                        _binding.content.likeCtv.isChecked =
                            (it.body.profile.get(0)?.isProfileLiked == 1)
                        _binding.content.shareCtv.isChecked =
                            (it.body.profile.get(0)?.isProfileShared == 1)
                        _binding.content.saveCtv.isChecked =
                            (it.body.profile.get(0)?.isProfileSaved == 1)

                    } else {
//                        msg.showShortMsg(getString(R.string.failed_to_fetch_analytics_data))
                    }
                },
                {
                    msg.showShortMsg(getString(R.string.something_wrong_try_again))
                })
        )

        viewModel.setUserName()
        _binding.content.ageValue.text = viewModel.getAge() ?: "NA"
        _binding.content.genderValue.text = viewModel.getGender() ?: "Male"
        _binding.content.heightValue.text = viewModel.getHeight() ?: "NA"
        viewModel.compositeDisposable.add(
            viewModel.getAllPortfolio()
                .subscribe({
                    if (it.responseCode == 200) {
                        viewModel.setPortfolioData(it)
                    } else {
                        msg.showShortMsg("Failed to load portfolio")
                    }
                }, {
                    msg.showShortMsg("Failed to load portfolio")
                })
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        menu?.get(0)?.isVisible = viewModel.getIsSelfView()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_profile_edit) {
            activityResultProfileUpdate1.launch(
                Intent(this, PersonalInfoActivity::class.java)
                    .putExtra("data", viewModel.profileDataLd.value)
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_dots -> {
                profileActionDialog.show(supportFragmentManager, "")
            }
            R.id.fab_add_media,
            R.id.btn_add_media -> {
                addMediaDialog.show(supportFragmentManager, "")
            }
            R.id.btn_add_post, R.id.fab_add -> {
                startActivity(Intent(this, CreatePostActivity::class.java))
            }

            R.id.like_ctv -> {
                viewModel.add(
                    viewModel.likeProfile(if (_binding.content.likeCtv.isChecked) 0 else 1)
                        .subscribe(
                            {
                                if (it.responseCode == 200) {
                                    _binding.content.likeCtv.isChecked =
                                        !_binding.content.likeCtv.isChecked
                                    if (_binding.content.likeCtv.isChecked) {
                                        like++
                                        _binding.content.likeCtv.text = "$like likes"
                                    } else {
                                        like--
                                        _binding.content.likeCtv.text = "$like likes"
                                    }
                                    msg.showShortMsg("Profile liked successfully")
                                }
                            },
                            {
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            })
                )
            }
            R.id.share_ctv -> {
                if (!_binding.content.shareCtv.isChecked) {
                    viewModel.add(
                        viewModel.shareProfile().subscribe(
                            {
                                if (it.responseCode == 200) {
                                    _binding.content.shareCtv.isChecked = true
                                    shareProfileLink()
                                    if (_binding.content.shareCtv.isChecked) {
                                        share++
                                        _binding.content.shareCtv.text = "$share Shares"
                                    } else {
                                        share--
                                        _binding.content.shareCtv.text = "$share Shares"
                                    }
                                }
                            },
                            {
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            })
                    )
                } else {
                    shareProfileLink()
                }
            }
            R.id.save_ctv -> {
                viewModel.add(
                    viewModel.saveProfile(if (_binding.content.saveCtv.isChecked) 0 else 1)
                        .subscribe(
                            {
                                if (it.responseCode == 200) {
                                    _binding.content.saveCtv.isChecked =
                                        !_binding.content.saveCtv.isChecked
                                    msg.showShortMsg("Profile saved successfully")
                                    if (_binding.content.saveCtv.isChecked) {
                                        save++
                                        _binding.content.saveCtv.text = "$save saves"
                                    } else {
                                        save--
                                        _binding.content.saveCtv.text = "$save saves"
                                    }
                                }
                            },
                            {
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            })
                )
            }
        }
    }

    private fun shareProfileLink() {
        val longUri =
            Uri.parse("https://texapp.page.link/?link=https://www.texapp.com/profile/${viewModel.getUserId()}/&apn=${packageName}")
        Firebase.dynamicLinks.shortLinkAsync {
            longLink = longUri
        }.addOnSuccessListener {
            it.shortLink?.let {
                IntentUtils.shareTextUrl(
                    this,
                    "Hey there, check out this amazing profile : $it"
                )
            }
        }.addOnFailureListener {
            IntentUtils.shareTextUrl(
                this,
                "Hey there, check out this amazing profile : $longUri"
            )
        }
    }

    override fun viewAnalyticsClicked() {
        startActivity(Intent(this, ViewAnalyticsActivity::class.java))
    }

    private val activityResultProfileSetting =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                viewModel.getUpdateProfileData(this)
            }
        }

    override fun profileSettingsClicked() {
        activityResultProfileSetting.launch(
            Intent(this, ProfileSettingsActivity::class.java)
                .putExtra("data", viewModel.profileDataLd.value)
        )
    }

    override fun savedPostClicked() {
        startActivity(Intent(this, SavedPostsActivity::class.java))
    }

    override fun shareProfileClicked() {
    }

    override fun aboutProfileClicked() {
        startActivity(
            Intent(this, ProfileAboutActivity::class.java)
                .putExtra("data", viewModel.profileDataLd.value)
        )
    }

    override fun addImageClicked() {
        pickImage()
    }

    override fun addVideoClicked() {
        pickVideo()
    }

    override fun addMusicClicked() {
        pickAudio()
    }

    override fun addDocumentClicked() {
        pickDocument()
    }

    private fun pickImage() {
        type = "image"
        permissionsLauncher.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                ) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
//
//        if (checkStoragePermission(
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                    Manifest.permission.READ_MEDIA_IMAGES
//                else
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                PERMISSION_CODE
//            )
//        ) {
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            activityResultImages.launch(
//                intent
//            )
//        } else {
//            msg.showIndefinteMsg(
//                "Please provide access to Image files!",
//                "Allow"
//            ) {
//                checkStoragePermission(
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                        Manifest.permission.READ_MEDIA_IMAGES
//                    else
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                    PERMISSION_CODE
//                )
//            }
//        }
    }

    private val activityResultDocument =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            fileUri = it.data?.data
            type = "document"
            fileUri?.let {
                pdfUtils = FileUtils.getPath(this, fileUri!!)
                    ?.let { it1 -> PdfUtils(this, filePath = it1) }
                loadDocument()
            }
        }

    private val activityResultAudio =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                fileUri = Objects.requireNonNull<Intent>(result.data).data
                type = "audio"
                var i = 0
                val url: Array<String> = arrayOf("", "")
                fileUri?.let {
                    NormalProgressDialog.showLoading(
                        this@ProfileActivity,
                        getString(R.string.media_file_uploading),
                        false
                    )
                    viewModel.compositeDisposable.add(viewModel.startUploadFilePost(
                        it,
                        thumbFileBmp!!
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ url[i++] = it },
                            {
                                NormalProgressDialog.stopLoading()
                                msg.showShortMsg(getString(R.string.failed_to_upload_file))
                                Log.e(TAG, "onCropSuccess: ", it)
                            }, {
                                viewModel.compositeDisposable.add(
                                    viewModel.createPortfolio(
                                        url[0],
                                        "",
                                        type
                                    )
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            if (it.responseCode == 200) {
                                                NormalProgressDialog.stopLoading()
                                                msg.showShortMsg("Portfolio Upload")
                                                callPortfolioApi()
                                            }
                                        }, {
                                            NormalProgressDialog.stopLoading()
                                            msg.showShortMsg(getString(R.string.failed_to_upload_portfolio))
                                        })
                                )
                            })
                    )
                }
            }
        }

    private val activityResultImages =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                fileUri = Objects.requireNonNull<Intent>(result.data).data
                type = "image"
                var i = 0
                val url: Array<String> = arrayOf("", "")
                fileUri?.let {
                    NormalProgressDialog.showLoading(
                        this@ProfileActivity,
                        getString(R.string.media_file_uploading),
                        false
                    )
                    thumbFileBmp = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(FileUtils.getPath(this, it)),
                        240,
                        320
                    )
                    viewModel.compositeDisposable.add(viewModel.startUploadFilePost(
                        it,
                        thumbFileBmp!!
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ url[i++] = it },
                            {
                                NormalProgressDialog.stopLoading()
                                msg.showShortMsg(getString(R.string.failed_to_upload_file))
                                Log.e(TAG, "onCropSuccess: ", it)
                            }, {
                                viewModel.compositeDisposable.add(
                                    viewModel.createPortfolio(
                                        url[0],
                                        url[1],
                                        type
                                    )
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            if (it.responseCode == 200) {
                                                msg.showShortMsg("Portfolio Upload")
                                                callPortfolioApi()
                                                NormalProgressDialog.stopLoading()
                                            }
                                        }, {
                                            NormalProgressDialog.stopLoading()
                                            msg.showShortMsg(getString(R.string.failed_to_upload_portfolio))
                                        })
                                )
                            })
                    )
                }
            }
        }

    private val activityResultVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                type = "video"
                fileUri = Objects.requireNonNull<Intent>(result.data).data
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(applicationContext, fileUri)
                val path = fileUri?.let { FileUtils.getPath(this, it) }
                if (path != null) {
                    thumbFileBmp = retriever.getFrameAtTime(2000)
                }
                retriever.release()
                var i = 0
                val url: Array<String> = arrayOf("", "")
                fileUri?.let {
                    NormalProgressDialog.showLoading(
                        this@ProfileActivity,
                        getString(R.string.media_file_uploading),
                        false
                    )
                    viewModel.compositeDisposable.add(viewModel.startUploadFilePost(
                        it,
                        thumbFileBmp!!
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ url[i++] = it },
                            {
                                NormalProgressDialog.stopLoading()
                                msg.showShortMsg(getString(R.string.failed_to_upload_file))
                                Log.e(TAG, "onCropSuccess: ", it)
                            }, {
                                viewModel.compositeDisposable.add(
                                    viewModel.createPortfolio(
                                        url[0],
                                        url[1],
                                        type
                                    )
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            if (it.responseCode == 200) {
                                                msg.showShortMsg("Portfolio Upload")
                                                callPortfolioApi()
                                                NormalProgressDialog.stopLoading()
                                            }
                                        }, {
                                            NormalProgressDialog.stopLoading()
                                            msg.showShortMsg(getString(R.string.failed_to_upload_portfolio))
                                        })
                                )
                            })
                    )
                }
            }
        }

    private fun pickVideo() {
        type = "video"
        permissionsLauncherVideo.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                ) else
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
//        if (checkStoragePermission(
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                    Manifest.permission.READ_MEDIA_VIDEO
//                else
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                PERMISSION_CODE
//            )
//        ) {
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "video/*"
//            activityResultVideo.launch(
//                intent
//            )
//        } else {
//            msg.showIndefinteMsg(
//                "Please provide access to Video files!",
//                "Allow"
//            ) {
//                checkStoragePermission(
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                        Manifest.permission.READ_MEDIA_VIDEO
//                    else
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                    PERMISSION_CODE
//                )
//            }
//        }
    }

    private fun pickAudio() {
        type = "audio"
        permissionsLauncherAudio.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                ) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
//        if (checkStoragePermission(
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                    Manifest.permission.READ_MEDIA_AUDIO
//                else
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                PERMISSION_CODE
//            )
//        ) {
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "audio/*"
//            activityResultAudio.launch(
//                intent
//            )
//        } else {
//            msg.showIndefinteMsg(
//                "Please provide access to Music files!",
//                "Allow"
//            ) {
//                checkStoragePermission(
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                        Manifest.permission.READ_MEDIA_AUDIO
//                    else
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                    PERMISSION_CODE
//                )
//            }
//        }
    }

    private fun pickDocument() {
        type = "document"
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
//        if (checkStoragePermission(
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                else
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                PERMISSION_CODE
//            )
//        ) {
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "application/pdf"
//            activityResultDocument.launch(
//                intent
//            )
//        } else {
//            msg.showIndefinteMsg(
//                "Please provide access to Document files!",
//                "Allow"
//            ) {
//                checkStoragePermission(
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                        Manifest.permission.READ_MEDIA_IMAGES
//                    else
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                    PERMISSION_CODE
//                )
//            }
//        }
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
                pdfUtils?.renderFirstPage(object : PdfUtils.PageReadyListener {
                    override fun onPagesReady(list: List<Bitmap>) {
                        Log.d(
                            TAG,
                            "onPagesReady: called with items count : " + list.size
                        )
                        if (list.isNotEmpty()) {
                            thumbFileBmp = ThumbnailUtils.extractThumbnail(
                                list[0], 300, 480
                            )
                            var i = 0
                            val url: Array<String> = arrayOf("", "")
                            fileUri?.let {
                                NormalProgressDialog.showLoading(
                                    this@ProfileActivity,
                                    getString(R.string.media_file_uploading),
                                    false
                                )
                                viewModel.startUploadFilePost(it, thumbFileBmp!!)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ url[i++] = it },
                                        {
                                            NormalProgressDialog.stopLoading()
                                            msg.showShortMsg(getString(R.string.failed_to_upload_file))
                                            Log.e(TAG, "onCropSuccess: ", it)
                                        }, {
                                            viewModel.compositeDisposable.add(
                                                viewModel.createPortfolio(
                                                    url[0],
                                                    url[1],
                                                    type
                                                )
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({
                                                        if (it.responseCode == 200) {
                                                            msg.showShortMsg("Portfolio Upload")
                                                            callPortfolioApi()
                                                            NormalProgressDialog.stopLoading()
                                                        }
                                                    }, {
                                                        Log.e(TAG, "onPagesReady: ", it)
                                                        NormalProgressDialog.stopLoading()
                                                        msg.showShortMsg(getString(R.string.failed_to_upload_portfolio))
                                                    })
                                            )
                                        })
                            }
                            //call api
                        } else {
                            msg.showShortMsg("Failed to load PDF File!")
                        }
                    }

                    override fun fail(e: Throwable) {
                        Log.e(TAG, "fail: ", e)
                    }

                    override fun complete() {
                        Log.d(TAG, "complete: called")
                    }
                })
            }
        })
    }

    private fun callPortfolioApi() {
        viewModel.compositeDisposable.add(
            viewModel.getAllPortfolio()
                .subscribe({
                    if (it.responseCode == 200) {
                        viewModel.setPortfolioData(it)
                    } else {
                        msg.showShortMsg("Failed to load portfolio")
                    }
                }, {
                    msg.showShortMsg("Failed to load portfolio")
                })
        )
    }

    data class CoverImage(
        val imageType: Int,
        val url: String
    )

}