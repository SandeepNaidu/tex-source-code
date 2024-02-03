package com.project.tex.aritist_profile.ui

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.project.tex.R
import com.project.tex.aritist_profile.RangeInputFilter
import com.project.tex.aritist_profile.adapter.CitiesAdapter
import com.project.tex.aritist_profile.dialog.CityDialog
import com.project.tex.aritist_profile.model.CitiesList
import com.project.tex.aritist_profile.model.UserProfileData
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.calendar.CalendarActivity
import com.project.tex.databinding.ActivityPersonalInfoBinding
import com.project.tex.main.ui.avid.avidCamera.view.NormalProgressDialog
import com.project.tex.utils.ViewUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateFormatUtils
import java.text.SimpleDateFormat
import java.util.*


class PersonalInfoActivity : BaseActivity<ActivityPersonalInfoBinding, ProfileViewModel>(),
    View.OnClickListener {

    override fun getViewBinding() = ActivityPersonalInfoBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(ProfileViewModel::class.java)
    private var profileImg: String? = null
    private var data: UserProfileData.Body.Users.Body? = null
    private val PERMISSION_CODE: Int = 123
    private val MALE: String = "Male"
    private val FEMALE: String = "Femal"
    private val OTHER: String = "OTHER"
    private var selectedGender = MALE
    private var otherGenderText = ""
    private var selectedCity = ""
    private var fileUri: Uri? = null
    private var age: Int = 1
    private var date: Long = 0L
    private val c = Calendar.getInstance()
    var permissionsList: ArrayList<String> = arrayListOf()
    var permissionsCount = 0
    var permissionAskedTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            data = intent?.getSerializableExtra("data", UserProfileData.Body.Users.Body::class.java)
        } else {
            data = intent?.getSerializableExtra("data") as? UserProfileData.Body.Users.Body?
        }

        data?.let {
            if (data?.profileImage != null) {
                profileImg = data?.profileImage
                fileUri = Uri.parse(profileImg)
                Glide.with(this).load(profileImg).error(R.drawable.default_user)
                    .into(_binding.profileImg)
            }
            if (data?.firstName != null || data?.lastName != null) {
                _binding.edtFullName.setText(
                    ((data?.firstName?.trim() ?: "") + " " + (data?.lastName?.trim()
                        ?: "")).removePrefix(" ").trim()
                )
            }
            if (data?.bio != null) {
                _binding.edtBio.setText(data?.bio?.trim() ?: "")
            }
            if (data?.gender != null) {
                if (data?.gender?.equals(MALE, true) == true) {
                    selectedGender = MALE
                    setBoxOutline(_binding.cardMale)
                } else if (data?.gender?.equals(FEMALE, true) == true) {
                    selectedGender = FEMALE
                    setBoxOutline(_binding.cardFemale)
                } else if (data?.gender != null && data?.gender?.isNotEmpty() == true) {
                    selectedGender = OTHER
                    otherGenderText = data?.gender ?: ""
                    clearStroke()
                    _binding.edtId.setText(data?.gender?.trim())
                    _binding.userIdTil.isVisible = true
                    _binding.otherTv.isVisible = false
                }
            }
            if (data?.dob != null) {
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ENGLISH)
                    val pasTime = data?.dob?.let { it1 -> dateFormat.parse(it1) }
                    // Display Selected date in textbox
                    if (pasTime != null) {
                        date = pasTime.time
                        _binding.edtId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                        _binding.edtId.setTextColor(ContextCompat.getColor(this, R.color.text_main))
                        _binding.edtId.typeface =
                            (ResourcesCompat.getFont(this, R.font.gilroy_bold))
                        val c1 = Calendar.getInstance()
                        c1.timeInMillis = date
                        age = calculateAge(c1.time)
                        _binding.dobName.text = "${c1.get(Calendar.DAY_OF_MONTH)} ${
                            SimpleDateFormat(
                                "MMMM"
                            ).format(c1.getTime())
                        } ${c1.get(Calendar.YEAR)}"
                        _binding.tvAge.text = "$age"
                        c.time = c1.time
                        _binding.cardAge.isVisible = true
                    }
                } catch (e: Exception) {

                }
            }
            if (data?.height != null) {
                _binding.heightName.setText(data?.height?.trim())
                _binding.heightCms.isVisible = data?.height?.trim()?.isNotEmpty() == true
            }
            if (data?.location != null) {
                selectedCity = data?.location ?: ""
                _binding.selectedCityTv.setText(data?.location?.trim())
            }
        }

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.heightName.filters = arrayOf(RangeInputFilter(0, 400))
        _binding.heightName.doAfterTextChanged {
            _binding.heightCms.isVisible = it.toString().isNotEmpty()
        }
        _binding.edtId.doAfterTextChanged {
            otherGenderText = it.toString()
        }
        _binding.male.setOnClickListener(this)
        _binding.female.setOnClickListener(this)
        _binding.other.setOnClickListener(this)
        _binding.btnUpdate.setOnClickListener(this)
        _binding.addImg.setOnClickListener(this)
        _binding.dobName.setOnClickListener(this)
        _binding.selectedCityTv.setOnClickListener(this)
    }

    private fun clearStroke() {
        _binding.cardFemale.strokeWidth = 2
        _binding.cardMale.strokeWidth = 2
        _binding.cardOther.strokeWidth = 2
        _binding.cardFemale.strokeColor = ContextCompat.getColor(this, R.color.text_hint_secondary)
        _binding.cardMale.strokeColor = ContextCompat.getColor(this, R.color.text_hint_secondary)
        _binding.cardOther.strokeColor = ContextCompat.getColor(this, R.color.text_hint_secondary)
    }

    private fun setBoxOutline(card: MaterialCardView) {
        card.strokeWidth = ViewUtils.dpToPx(this, 2)
        card.strokeColor = ContextCompat.getColor(this, R.color.colorPrimary)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.male -> {
                if (selectedGender == MALE) return
                selectedGender = MALE
                clearStroke()
                setBoxOutline(v.parent as MaterialCardView)
                _binding.userIdTil.isVisible = false
                _binding.otherTv.isVisible = true
                _binding.edtId.text?.clear()
            }
            R.id.female -> {
                if (selectedGender == FEMALE) return
                selectedGender = FEMALE
                clearStroke()
                setBoxOutline(v.parent as MaterialCardView)
                _binding.userIdTil.isVisible = false
                _binding.otherTv.isVisible = true
                _binding.edtId.text?.clear()
            }
            R.id.other -> {
                if (selectedGender == OTHER) return
                selectedGender = OTHER
                clearStroke()
                _binding.userIdTil.isVisible = true
                _binding.otherTv.isVisible = false
            }
            R.id.add_img -> {
                pickImage()
            }
            R.id.dob_name -> {
                mStartForResult.launch(Intent(this, CalendarActivity::class.java))
            }
            R.id.selected_city_tv -> {
                CityDialog().apply {
                    setListener(object : CitiesAdapter.ClickAction {
                        override fun itemClick(item: CitiesList.CitiesListItem) {
                            selectedCity = item.name ?: "" + ", " + item.state ?: ""
                            this@PersonalInfoActivity._binding.selectedCityTv.text = selectedCity
                        }
                    })
                    show(supportFragmentManager, "city dialog")
                }
            }
            R.id.btn_update -> {
                val firstName = _binding.edtFullName.text.toString()
                val gender = if (selectedGender == OTHER) (otherGenderText.ifEmpty { OTHER })
                else selectedGender
                val dob = DateFormatUtils.format(date, "yyyy-MM-dd")
                val height = _binding.heightName.text.toString()
                if (fileUri == null || fileUri!!.path?.isEmpty() == true) {
                    msg.showShortMsg("Please select the profile image")
                } else if (firstName.isEmpty()) {
                    msg.showShortMsg("Please enter the name")
                } else if (selectedGender.isEmpty()) {
                    msg.showShortMsg("Please select the gender")
                } else if (dob.isEmpty()) {
                    msg.showShortMsg("Please select the date of birth")
                } else if (height.isEmpty()) {
                    msg.showShortMsg("Please enter your height")
                } else if (height.length < 2) {
                    msg.showShortMsg("Please enter a valid height")
                } else if (selectedCity.isEmpty()) {
                    msg.showShortMsg("Please select the city")
                } else {
                    fileUri?.let {
                        NormalProgressDialog.showLoading(this, "Updating Profile", false)
                        if (profileImg != null && StringUtils.equals(
                                profileImg,
                                fileUri?.toString()
                            )
                        ) {
                            viewModel.compositeDisposable.add(
                                viewModel.updateProfile(
                                    firstName,
                                    "",
                                    _binding.edtBio.text.toString(),
                                    gender,
                                    dob,
                                    _binding.tvAge.text.toString(),
                                    _binding.heightName.text.toString(),
                                    _binding.selectedCityTv.text.toString(),
                                    profileImg!!
                                ).subscribe({
                                    NormalProgressDialog.stopLoading(this)
                                    setResult(RESULT_OK)
                                    finish()
                                }, {
                                    NormalProgressDialog.stopLoading(this)
                                    msg.showShortMsg("Failed to update the profile")
                                })
                            )
                        } else {
                            viewModel.startUploadProfileImg(it)
                                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                                    profileImg = it
                                }, {
                                    NormalProgressDialog.stopLoading(this)
                                    msg.showShortMsg("Failed to upload the profile image")
                                }, {
                                    viewModel.compositeDisposable.add(
                                        viewModel.updateProfile(
                                            firstName,
                                            "",
                                            _binding.edtBio.text.toString(),
                                            gender,
                                            dob,
                                            _binding.tvAge.text.toString(),
                                            _binding.heightName.text.toString(),
                                            _binding.selectedCityTv.text.toString(),
                                            profileImg!!
                                        ).subscribe({
                                            viewModel.updateProfileImageLastUpdate()
                                            NormalProgressDialog.stopLoading(this)
                                            setResult(RESULT_OK)
                                            finish()
                                        }, {
                                            NormalProgressDialog.stopLoading(this)
                                            msg.showShortMsg("Failed to update the profile")
                                        })
                                    )
                                })
                        }
                    }
                }
            }
        }
    }

    private val activityResultImages =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                fileUri = Objects.requireNonNull<Intent>(result.data).data
                Glide.with(this).load(fileUri).error(R.drawable.default_user)
                    .into(_binding.profileImg)
            }
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
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                activityResultImages.launch(
                    intent
                )
            }
        }

    private fun askForPermissions(permissionsList: ArrayList<String>) {
        val newPermissionStr = Array(permissionsList.size) {
            permissionsList[it]
        }

        if (newPermissionStr.isNotEmpty()) {
            permissionsLauncher.launch(newPermissionStr)
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

    private fun pickImage() {
        permissionsLauncher.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                ) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
//        if (checkStoragePermission(
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                    Manifest.permission.READ_MEDIA_IMAGES
//                else
//                    Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE
//            )
//        ) {
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            activityResultImages.launch(
//                intent
//            )
//        } else {
//            msg.showIndefinteMsg(
//                "Please provide storage permission", "Allow"
//            ) {
//                checkStoragePermission(
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                        Manifest.permission.READ_MEDIA_IMAGES
//                    else
//                        Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE
//                )
//            }
//        }
    }


    private val mStartForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                date = data.getLongExtra("date", 0L)
                if (date != 0L) {
                    // Display Selected date in textbox
                    _binding.edtId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                    _binding.edtId.setTextColor(ContextCompat.getColor(this, R.color.text_main))
                    _binding.edtId.typeface = (ResourcesCompat.getFont(this, R.font.gilroy_bold))
                    val c1 = Calendar.getInstance()
                    c1.timeInMillis = date
                    age = calculateAge(c1.time)
                    _binding.dobName.text = "${c1.get(Calendar.DAY_OF_MONTH)} ${
                        SimpleDateFormat(
                            "MMMM"
                        ).format(c1.getTime())
                    } ${c1.get(Calendar.YEAR)}"
                    _binding.tvAge.text = "$age"
                    c.time = c1.time
                    _binding.cardAge.isVisible = true
                }
            }
        }
    }

    private fun calculateAge(birthdate: Date?): Int {
        val birth = Calendar.getInstance()
        if (birthdate != null) {
            birth.time = birthdate
        }
        val today = Calendar.getInstance()
        var yearDifference = (today[Calendar.YEAR] - birth[Calendar.YEAR])
        if (today[Calendar.MONTH] < birth[Calendar.MONTH]) {
            yearDifference--
        } else {
            if (today[Calendar.MONTH] == birth[Calendar.MONTH] && today[Calendar.DAY_OF_MONTH] < birth[Calendar.DAY_OF_MONTH]) {
                yearDifference--
            }
        }
        return yearDifference
    }
}