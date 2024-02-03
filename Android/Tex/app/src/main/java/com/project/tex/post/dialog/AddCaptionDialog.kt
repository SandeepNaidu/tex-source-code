package com.project.tex.post.dialog

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.project.tex.R
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogAddCaptionBinding
import com.project.tex.post.ui.CreatePostActivity
import com.volokh.danylo.hashtaghelper.HashTagHelper


class AddCaptionDialog : BaseDialog<DialogAddCaptionBinding>(), View.OnClickListener {
    private var listener: CaptionAddedCallback? = null
    private var mEditTextHashTagHelper: HashTagHelper? = null

    override fun getViewBinding() = DialogAddCaptionBinding.inflate(layoutInflater)

    fun showDialog(manager: FragmentManager, listener: CaptionAddedCallback) {
        val fragment = AddCaptionDialog()
        fragment.setListener(listener)
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

    public fun setListener(listener: CaptionAddedCallback) {
        this.listener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Here we don't specify additionalSymbols.
        // It means that in EditText only letters and digits will be valid symbols
        mEditTextHashTagHelper =
            HashTagHelper.Creator.create(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_main
                ), null
            )
        mEditTextHashTagHelper?.handle(_binding.edtCaption)
        _binding.btnNext.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                if (_binding.edtCaption.text?.isEmpty() == true) {
                    (context as? CreatePostActivity)?.msg?.showShortMsg("Please enter some caption for the Post")
                    return
                }
                listener?.onCaptionAdded(
                    _binding.edtCaption.text.toString(),
                    mEditTextHashTagHelper!!.allHashTags.joinToString(separator = "|")
                )
                dismissAllowingStateLoss()
            }
        }
    }

    interface CaptionAddedCallback {
        fun onCaptionAdded(caption: String, joinToString: String)
    }
}