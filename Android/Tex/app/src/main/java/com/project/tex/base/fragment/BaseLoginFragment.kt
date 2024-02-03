package com.project.tex.base.fragment

import androidx.viewbinding.ViewBinding
import com.project.tex.base.activity.BaseOnboarding

abstract class BaseLoginFragment<VB : ViewBinding> : BaseFragment<VB>() {

    val activity by lazy{
        getActivity() as BaseOnboarding<*,*>
    }
}