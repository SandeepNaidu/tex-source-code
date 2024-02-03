package com.project.tex.onboarding.dialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.FragmentManager
import com.project.tex.R
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogLoginErrorInfoBinding
import com.project.tex.onboarding.login.QuickSignInActivity
import com.project.tex.onboarding.signup.SignUpActivity
import com.project.tex.utils.MyClickableSpanUnderline

class LoginErrorDialog : BaseDialog<DialogLoginErrorInfoBinding>(), View.OnClickListener {
    override fun getViewBinding() = DialogLoginErrorInfoBinding.inflate(layoutInflater)

    fun showDialog(manager: FragmentManager) {
        val fragment = LoginErrorDialog()
        val args = Bundle()
        fragment.setArguments(args)
        fragment.show(manager, "login_error")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spannable: Spannable = SpannableString(_binding.btnRetry.text.toString())

        spannable.setSpan(
            MyClickableSpanUnderline(requireContext()) {
                (activity as? QuickSignInActivity)?.clearText()
                dismissAllowingStateLoss()
            },
            0,
            5,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        _binding.btnRetry.movementMethod = LinkMovementMethod.getInstance()
        _binding.btnRetry.highlightColor = Color.TRANSPARENT
        _binding.btnRetry.text = spannable
        _binding.createAccount.setOnClickListener(this)
        _binding.closeIv.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.createAccount -> {
                dismissAllowingStateLoss()
                context?.startActivity(Intent(context, SignUpActivity::class.java))
            }
            R.id.close_iv -> {
                dismissAllowingStateLoss()
            }
        }
    }


}