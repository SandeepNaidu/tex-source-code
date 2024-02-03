package com.project.tex.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.project.tex.base.activity.BaseActivity
import com.project.tex.base.data.SharedPreference
import com.project.tex.utils.MsgUtils

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var binding: VB? = null
    val _binding get() = binding!!

    val msg: MsgUtils by lazy {
        (activity as? BaseActivity<*,*>)?.msg!!.apply {
            updateSnackbarView(requireView().rootView)
        }
    }

    val dataKeyStore: SharedPreference by lazy {
        (activity as? BaseActivity<*,*>)?.dataKeyValue!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        return _binding.root
    }

    abstract fun getViewBinding(): VB
}