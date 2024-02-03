package com.project.tex.base.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.project.tex.databinding.ActivityActionBarBinding

abstract class ActionBarActivity<VM : ViewModel> : BaseActivity<ActivityActionBarBinding, VM>() {
    override fun getViewBinding() = ActivityActionBarBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(_binding.mainToolbar)
        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun setTitle(title: String) {
        _binding.mainToolbar.title = title
    }

    final fun setView(view: View) {
        _binding.container.addView(view)
    }

}