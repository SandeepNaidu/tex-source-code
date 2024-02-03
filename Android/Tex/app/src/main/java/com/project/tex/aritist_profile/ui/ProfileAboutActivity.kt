package com.project.tex.aritist_profile.ui

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.tex.R
import com.project.tex.aritist_profile.model.UserProfileData
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityProfileAboutBinding
import com.project.tex.utils.TimeAgo2

class ProfileAboutActivity : BaseActivity<ActivityProfileAboutBinding, ProfileViewModel>(),
    View.OnClickListener {

    private var data: UserProfileData.Body.Users.Body? = null

    override fun getViewBinding() = ActivityProfileAboutBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(ProfileViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            data = intent?.getSerializableExtra("data", UserProfileData.Body.Users.Body::class.java)
        } else {
            data = intent?.getSerializableExtra("data") as? UserProfileData.Body.Users.Body?
        }

        data?.let {
            Glide.with(this).load(data?.profileImage).into(_binding.profileImg)
            val contactLastUpdated =
                TimeAgo2().covertTimeToText(if (TextUtils.isEmpty(it.contactLastUpdatedOn)) it.creationDate else it.contactLastUpdatedOn)
            _binding.contactLastUpdate.text = "Contact information updated less than $contactLastUpdated"
            val profileLastUpdated =
                TimeAgo2().covertTimeToText(if (TextUtils.isEmpty(it.profileImageLastUpdatedOn)) it.creationDate else it.profileImageLastUpdatedOn)
            _binding.profileLastFrom.text = "Profile photo updated less than $profileLastUpdated"
        }

        viewModel.setUserName()
        viewModel.userNameData.observe(this, Observer {
            _binding.usernameTitle.text = it
        })

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.privacy -> {

            }
            R.id.terms_of_use -> {

            }
            R.id.open_source -> {

            }
        }
    }

}