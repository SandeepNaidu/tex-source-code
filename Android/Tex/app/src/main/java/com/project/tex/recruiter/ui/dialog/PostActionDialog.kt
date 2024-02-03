package com.project.tex.recruiter.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.project.tex.R
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogPostActionBinding
import com.project.tex.main.ui.dialog.PostActionDialog

class PostActionDialog : BaseDialog<DialogPostActionBinding>(), View.OnClickListener {
    override fun getViewBinding() = DialogPostActionBinding.inflate(layoutInflater)

    fun showDialog(manager: FragmentManager) {
        val fragment = PostActionDialog()
        val args = Bundle()
        fragment.setArguments(args)
        fragment.show(manager, "post_action")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.shareTv.setOnClickListener(this)
        _binding.linkTv.setOnClickListener(this)
        _binding.saveTv.setOnClickListener(this)
        _binding.infoTv.setOnClickListener(this)
        _binding.reportTv.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.share_tv -> {
                dismissAllowingStateLoss()
            }
            R.id.save_tv -> {
                dismissAllowingStateLoss()
            }
            R.id.link_tv -> {
                dismissAllowingStateLoss()
            }
            R.id.info_tv -> {
                dismissAllowingStateLoss()
            }
            R.id.report_tv -> {
                dismissAllowingStateLoss()
            }
        }
    }


}