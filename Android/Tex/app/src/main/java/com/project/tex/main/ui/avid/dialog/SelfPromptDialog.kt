package com.project.tex.main.ui.avid.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogSelfPromptInputBinding
import com.project.tex.main.ui.search.AvidViewModel

class SelfPromptDialog : BaseDialog<DialogSelfPromptInputBinding>(), View.OnClickListener {

    override fun getViewBinding() = DialogSelfPromptInputBinding.inflate(layoutInflater)

    private val viewModel by lazy { activity?.let { ViewModelProvider(it).get(AvidViewModel::class.java) } }

    fun showDialog(manager: FragmentManager, text: String) {
        val fragment = SelfPromptDialog()
        val args = Bundle()
        args.putString("lasttext", text)
        fragment.arguments = args
        fragment.show(manager, "self_prompt")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = arguments?.getString("lasttext") ?: ""
        _binding.inputBox.setText(text)
        _binding.btnNext.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                viewModel?.setText(_binding.inputBox.text.toString())
                dismissAllowingStateLoss()
            }
        }
    }

    override fun isCancelable(): Boolean {
        return true
    }

}