package com.project.tex.main.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.project.tex.R
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogCreatePostBinding
import com.project.tex.main.ui.home.HomeFragment

class CreatePostDialog : BaseDialog<DialogCreatePostBinding>(), View.OnClickListener {
    private var listener: CreatePostClicks? = null

    override fun getViewBinding() = DialogCreatePostBinding.inflate(layoutInflater)

    fun showDialog(manager: FragmentManager) {
        val fragment = CreatePostDialog()
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

        if (parentFragment is HomeFragment) {
            listener = ((parentFragment as? HomeFragment) as CreatePostClicks)
        }
        _binding.createImage.setOnClickListener(this)
        _binding.createVideo.setOnClickListener(this)
        _binding.createMusic.setOnClickListener(this)
        _binding.createDoc.setOnClickListener(this)
        _binding.createEvent.setOnClickListener(this)
        _binding.createPoll.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.create_video -> {
                listener?.createVideoClick()
                dismissAllowingStateLoss()
            }
            R.id.create_image -> {
                listener?.createImageClick()
                dismissAllowingStateLoss()
            }
            R.id.create_music -> {
                listener?.createAudioClick()
                dismissAllowingStateLoss()
            }
            R.id.create_doc -> {
                listener?.createDocumentClick()
                dismissAllowingStateLoss()
            }
            R.id.create_event -> {
                listener?.createEventClick()
                dismissAllowingStateLoss()
            }
            R.id.create_poll -> {
                listener?.createPollClick()
                dismissAllowingStateLoss()
            }
        }
    }

    interface CreatePostClicks {
        fun createVideoClick()
        fun createImageClick()
        fun createAudioClick()
        fun createDocumentClick()
        fun createEventClick()
        fun createPollClick()
    }
}