package com.project.tex.recruiter.ui.jobs

import android.os.Bundle
import android.view.View
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentRecJobsBinding

class RecJobsFragment : BaseFragment<FragmentRecJobsBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun getViewBinding() = FragmentRecJobsBinding.inflate(layoutInflater)
}