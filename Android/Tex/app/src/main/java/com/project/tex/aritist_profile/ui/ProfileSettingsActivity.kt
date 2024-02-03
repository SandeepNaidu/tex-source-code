package com.project.tex.aritist_profile.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.project.tex.aritist_profile.model.UserProfileData
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityProfileSettingBinding
import com.project.tex.settings.adapter.SettingAdapter

class ProfileSettingsActivity : BaseActivity<ActivityProfileSettingBinding, ProfileViewModel>(),
    SettingAdapter.OnClickListener {

    override fun getViewBinding() = ActivityProfileSettingBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(ProfileViewModel::class.java)

    private var userData: UserProfileData.Body.Users.Body? = null
    private val activityResultUpdate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                userData = it.data?.getSerializableExtra("data") as UserProfileData.Body.Users.Body?
                setResult(RESULT_OK, Intent().putExtra("data", userData))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            userData =
                intent?.getSerializableExtra("data", UserProfileData.Body.Users.Body::class.java)
        } else {
            userData = intent?.getSerializableExtra("data") as? UserProfileData.Body.Users.Body?
        }

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }

        _binding.settingRv.adapter = SettingAdapter(viewModel.getProfileSettingPageItem(), this)

    }

    override fun itemClicked(pos: Int, id: Int) {
        when (id) {
            HELP -> {
                activityResultUpdate.launch(
                    Intent(this, StatusSettingsActivity::class.java)
                        .putExtra("data", userData)
                )
            }
            ABOUT -> {
                activityResultUpdate.launch(
                    Intent(this, ContactDetailsctivity::class.java)
                        .putExtra("data", userData)
                )
            }
        }
    }

    companion object {
        const val HELP: Int = 0
        const val ABOUT: Int = 1
    }
}