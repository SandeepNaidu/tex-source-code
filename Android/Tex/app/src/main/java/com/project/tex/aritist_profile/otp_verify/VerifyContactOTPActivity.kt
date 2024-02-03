package com.project.tex.aritist_profile.otp_verify

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseOnboarding
import com.project.tex.databinding.ActivityVerifyContactOTPBinding
import com.project.tex.onboarding.otp.OtpVerifyViewModel
import com.project.tex.utils.KeyboardUtils

class VerifyContactOTPActivity :
    BaseOnboarding<ActivityVerifyContactOTPBinding, OtpVerifyViewModel>() {

    override fun getViewBinding() = ActivityVerifyContactOTPBinding.inflate(layoutInflater)

    override fun getViewModelInstance() =
        ViewModelProvider(this).get(OtpVerifyViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msg.updateSnackbarView(_binding.root)
        val id: String? = intent.getStringExtra("id")
        _binding.tvEnter4di.text = "Please enter four digit OTP sent to mobile number $id"

        _binding.pinview.addTextChangedListener {
            setPinViewLineColor(R.color.text_main)
            it?.toString()?.let { code ->
                _binding.tvResult.isVisible = false
                if (code.isEmpty() || code.length < 4) {
                } else {
                    if (code == "0000") {
                        _binding.pinview.clearFocus()
                        showSuccess()
                        KeyboardUtils.hideKeyboard(this)
                        _binding.root.postDelayed({
                            setResult(RESULT_OK)
                            finish()
                        }, 1000)
                    } else {
                        showError()
                        msg.showShortMsg("Invalid OTP")
                    }
                }
            }
        }
        //Back
        _binding.backBtn.setOnClickListener { onBackPressed() }
    }

    private fun setPinViewLineColor(@ColorRes color: Int) {
        _binding.pinview.setLineColor(
            ContextCompat.getColor(
                this,
                color
            )
        )
    }

    private fun showSuccess() {
        setPinViewLineColor(R.color.text_underline_success)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            _binding.tvResult,
            R.drawable.ic_done,
            0,
            0,
            0
        )
        _binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.text_underline_success))
        _binding.tvResult.text = getString(R.string.verification_success)
        _binding.tvResult.isVisible = true
        _binding.tvDidntReceiveCode.isVisible = false
        _binding.tvTimer.isVisible = false
    }

    private fun showError() {
        _binding.tvResult.text = getString(R.string.text_verification_failed_try_again)
        _binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.text_alert_red))
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            _binding.tvResult,
            R.drawable.ic_close_red,
            0,
            0,
            0
        )
        setPinViewLineColor(R.color.text_alert_red)

        _binding.tvResult.isVisible = true
    }

}