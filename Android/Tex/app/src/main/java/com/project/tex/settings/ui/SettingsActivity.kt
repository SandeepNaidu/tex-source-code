package com.project.tex.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivitySettingsBinding
import com.project.tex.settings.adapter.SettingAdapter
import com.project.tex.settings.viewmodel.SettingViewModel

class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingViewModel>(),
    SettingAdapter.OnClickListener {

    override fun getViewBinding() = ActivitySettingsBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(SettingViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }

        _binding.settingRv.adapter = SettingAdapter(viewModel.getSettingPageItem(), this)
    }

    override fun itemClicked(pos: Int, id: Int) {
        when (id) {
            INVITE_FRIEND -> {
                startActivity(Intent(this, InviteFriendsActivity::class.java))
            }
            NOTIFICATION -> {
                startActivity(Intent(this, NotificationActivity::class.java))
            }
            SECURITY -> {
                startActivity(Intent(this, SecurityActivity::class.java))
            }
            PRIVACY -> {

            }
            HELP -> {
                startActivity(Intent(this, HelpActivity::class.java))
            }
            ABOUT -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
    }

    companion object {
        const val INVITE_FRIEND: Int = 0
        const val NOTIFICATION: Int = 1
        const val PRIVACY: Int = 2
        const val SECURITY: Int = 3
        const val HELP: Int = 4
        const val ABOUT: Int = 5
    }

}