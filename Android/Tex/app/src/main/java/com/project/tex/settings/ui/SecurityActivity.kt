package com.project.tex.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivitySecurityBinding
import com.project.tex.settings.viewmodel.SettingViewModel

class SecurityActivity : BaseActivity<ActivitySecurityBinding, SettingViewModel>(),
    View.OnClickListener {

    override fun getViewBinding() = ActivitySecurityBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(SettingViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.emailTv.isChecked = viewModel.getIsEnabledSecurity()

        _binding.linkedDeviceTv.setOnClickListener(this)
        _binding.emailTv.setOnClickListener(this)
        _binding.smsTv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.linked_device_tv->{

            }
            R.id.email_tv->{
//                _binding.emailTv.isChecked = !_binding.emailTv.isChecked
//                viewModel.setIsEnabledSecurity(_binding.emailTv.isChecked)
            }
            R.id.sms_tv->{
                _binding.smsTv.isChecked = !_binding.smsTv.isChecked

            }
        }
    }

}