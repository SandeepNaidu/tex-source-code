package com.project.tex.aritist_profile.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.project.tex.R
import com.project.tex.aritist_profile.ui.PostFragment
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogFilterPostBinding

@Suppress("DEPRECATION", "RedundantOverride")
class FilterPostDialog : BaseDialog<DialogFilterPostBinding>(), View.OnClickListener {
    override fun getViewBinding() = DialogFilterPostBinding.inflate(layoutInflater)

    var mListener: ClickAction? = null

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
        mListener = parentFragment as PostFragment

        arguments?.let { args ->
        }
        _binding.avidTv.setOnClickListener(this)
        _binding.postTv.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.avid_tv -> {
                mListener?.filterByAvid()
                dismissAllowingStateLoss()
            }
            R.id.post_tv -> {
                mListener?.filterByPost()
                dismissAllowingStateLoss()
            }
        }
    }

    interface ClickAction{
        fun filterByAvid()
        fun filterByPost()
    }

}