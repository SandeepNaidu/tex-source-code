package com.project.tex.aritist_profile.dialog

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.project.tex.R
import com.project.tex.aritist_profile.ui.ProfileActivity
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogPostActionBinding
import com.project.tex.databinding.DialogProfileActionBinding
import com.project.tex.main.ui.adapter.PostAdapter
import com.project.tex.main.ui.home.HomeFragment
import com.project.tex.post.model.AllPostData
import com.project.tex.recruiter.ui.dialog.PostActionDialog
import org.apache.commons.lang3.StringUtils

@Suppress("DEPRECATION", "RedundantOverride")
class ProfileActionDialog : BaseDialog<DialogProfileActionBinding>(), View.OnClickListener {
    override fun getViewBinding() = DialogProfileActionBinding.inflate(layoutInflater)

    var mListener: ProfileActionDialog.ClickAction? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener = context as ProfileActivity

        arguments?.let { args ->
        }
        _binding.viewAnalyticTv.setOnClickListener(this)
        _binding.profileSettingTv.setOnClickListener(this)
        _binding.savedPostTv.setOnClickListener(this)
        _binding.shareProfileTv.setOnClickListener(this)
        _binding.aboutProfileTv.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.view_analytic_tv -> {
                mListener?.viewAnalyticsClicked()
                dismissAllowingStateLoss()
            }
            R.id.profile_setting_tv -> {
                mListener?.profileSettingsClicked()
                dismissAllowingStateLoss()
            }
            R.id.saved_post_tv -> {
                mListener?.savedPostClicked()
                dismissAllowingStateLoss()
            }
            R.id.share_profile_tv -> {
                mListener?.shareProfileClicked()
                dismissAllowingStateLoss()
            }
            R.id.about_profile_tv -> {
                mListener?.aboutProfileClicked()
                dismissAllowingStateLoss()
            }
        }
    }

    interface ClickAction{
        fun viewAnalyticsClicked()
        fun profileSettingsClicked()
        fun savedPostClicked()
        fun shareProfileClicked()
        fun aboutProfileClicked()
    }

}