package com.project.tex.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityNotificationBinding
import com.project.tex.settings.viewmodel.SettingViewModel

class NotificationActivity : BaseActivity<ActivityNotificationBinding, SettingViewModel>(),
    View.OnClickListener {

    override fun getViewBinding() = ActivityNotificationBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(SettingViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        _binding.mainToolbar.setTitle(R.string.notifications)
        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.pauseAllTv.isChecked = viewModel.getIsCompleteNotificationPaused()
        _binding.emailTv.isChecked = viewModel.getEmailNotificationPaused()
        _binding.smsTv.isChecked = viewModel.getSMSNotificationPaused()
        _binding.pauseAllTv.setOnClickListener(this)
        _binding.emailTv.setOnClickListener(this)
        _binding.smsTv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pause_all_tv -> {
                _binding.pauseAllTv.isChecked = !_binding.pauseAllTv.isChecked
                viewModel.setIsCompleteNotificationPaused(_binding.pauseAllTv.isChecked)
            }
            R.id.email_tv -> {
                _binding.emailTv.isChecked = !_binding.emailTv.isChecked
                viewModel.setEmailNotificationPaused(_binding.pauseAllTv.isChecked)
            }
            R.id.sms_tv -> {
                _binding.smsTv.isChecked = !_binding.smsTv.isChecked
                viewModel.setSMSNotificationPaused(_binding.pauseAllTv.isChecked)
            }
        }
    }

}