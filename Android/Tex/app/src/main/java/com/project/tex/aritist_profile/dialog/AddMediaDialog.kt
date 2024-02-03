package com.project.tex.aritist_profile.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.project.tex.R
import com.project.tex.aritist_profile.ui.ProfileActivity
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogAddMediaBinding

@Suppress("DEPRECATION", "RedundantOverride")
class AddMediaDialog : BaseDialog<DialogAddMediaBinding>(), View.OnClickListener {
    override fun getViewBinding() = DialogAddMediaBinding.inflate(layoutInflater)

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
        mListener = context as ProfileActivity

        arguments?.let { args ->
        }
        _binding.addImageTv.setOnClickListener(this)
        _binding.addVideoTv.setOnClickListener(this)
        _binding.addMusicTv.setOnClickListener(this)
        _binding.addDocumentTv.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_image_tv -> {
                mListener?.addImageClicked()
                dismissAllowingStateLoss()
            }
            R.id.add_video_tv -> {
                mListener?.addVideoClicked()
                dismissAllowingStateLoss()
            }
            R.id.add_music_tv -> {
                mListener?.addMusicClicked()
                dismissAllowingStateLoss()
            }
            R.id.add_document_tv -> {
                mListener?.addDocumentClicked()
                dismissAllowingStateLoss()
            }
        }
    }

    interface ClickAction{
        fun addImageClicked()
        fun addVideoClicked()
        fun addMusicClicked()
        fun addDocumentClicked()
    }

}