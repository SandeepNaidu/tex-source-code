package com.project.tex.aritist_profile.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.project.tex.R
import com.project.tex.aritist_profile.ui.PostFragment
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogSortPostBinding

@Suppress("DEPRECATION", "RedundantOverride")
class SortPostDialog : BaseDialog<DialogSortPostBinding>(), View.OnClickListener {
    override fun getViewBinding() = DialogSortPostBinding.inflate(layoutInflater)

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
        _binding.dateTv.setOnClickListener(this)
        _binding.popularTv.setOnClickListener(this)
        _binding.mostViewedTv.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.date_tv -> {
                mListener?.byDate()
                dismissAllowingStateLoss()
            }
            R.id.popular_tv -> {
                mListener?.byPopularity()
                dismissAllowingStateLoss()
            }
            R.id.most_viewed_tv -> {
                mListener?.byMostViewed()
                dismissAllowingStateLoss()
            }
        }
    }

    interface ClickAction{
        fun byDate()
        fun byPopularity()
        fun byMostViewed()
    }

}