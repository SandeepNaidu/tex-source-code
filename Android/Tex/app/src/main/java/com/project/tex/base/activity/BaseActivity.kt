package com.project.tex.base.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.project.tex.base.data.SharedPreference
import com.project.tex.utils.MSG_TYPE
import com.project.tex.utils.MsgUtils

abstract class BaseActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {
    val msg: MsgUtils by lazy {
        MsgUtils(this, msgType = MSG_TYPE.TOAST)
    }

    val dataKeyValue: SharedPreference by lazy {
        SharedPreference(this)
    }

    private var binding: VB? = null
    val _binding get() = binding!!

    private var _viewModel: VM? = null
    val viewModel get() = _viewModel!!

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = getViewBinding()
        setContentView(binding!!.root)
        _viewModel = getViewModelInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding!!.root)
        _viewModel = getViewModelInstance()
    }

    abstract fun getViewBinding(): VB
    abstract fun getViewModelInstance(): VM

    fun checkStoragePermission(permission: String, PERMISSION_CODE: Int): Boolean {
        return if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(permission)
            requestPermissions(
                permissions,
                PERMISSION_CODE
            )
            false
        } else {
            true
        }
    }

    fun hasStoragePermission(permission: String): Boolean {
        return checkSelfPermission(permission) != PackageManager.PERMISSION_DENIED
    }
}