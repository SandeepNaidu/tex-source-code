package com.project.tex.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityAboutBinding
import com.project.tex.settings.viewmodel.SettingViewModel

class AboutActivity : BaseActivity<ActivityAboutBinding, SettingViewModel>(),
    View.OnClickListener {

    override fun getViewBinding() = ActivityAboutBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(SettingViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }

        _binding.privacy.setOnClickListener(this)
        _binding.termsOfUse.setOnClickListener(this)
        _binding.openSource.setOnClickListener(this)

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