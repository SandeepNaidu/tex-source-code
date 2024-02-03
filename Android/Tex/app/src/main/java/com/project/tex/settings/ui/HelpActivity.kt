package com.project.tex.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityHelpBinding
import com.project.tex.settings.adapter.FaqAdapter
import com.project.tex.settings.viewmodel.SettingViewModel

class HelpActivity : BaseActivity<ActivityHelpBinding, SettingViewModel>(),
    View.OnClickListener {

    override fun getViewBinding() = ActivityHelpBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(SettingViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }

        _binding.recFaqs.adapter = FaqAdapter(viewModel.getFaqs())

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.linked_device_tv->{

            }
            R.id.email_tv->{

            }
            R.id.sms_tv->{

            }
        }
    }

}