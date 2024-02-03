package com.project.tex.onboarding.otp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseOnboarding
import com.project.tex.base.data.SharedPrefsKey
import com.project.tex.databinding.ActivityVerifyOTPBinding
import com.project.tex.firebase.FirebaseDBManager
import com.project.tex.main.HomeActivity
import com.project.tex.onboarding.Presets
import com.project.tex.onboarding.model.LoginResponse
import com.project.tex.utils.KeyboardUtils
import org.apache.commons.lang3.StringUtils

class VerifyOTPActivity : BaseOnboarding<ActivityVerifyOTPBinding, OtpViewModel>() {

    override fun getViewBinding() = ActivityVerifyOTPBinding.inflate(layoutInflater)

    override fun getViewModelInstance() = ViewModelProvider(this).get(OtpViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msg.updateSnackbarView(_binding.root)
        val phonenumber: String? = intent.getStringExtra("user_input")
        val name: String? = intent.getStringExtra("name")
        val type: String? = intent.getStringExtra("idType")
        _binding.userAddress.text = phonenumber
        _binding.tvUsername.text = name
        _binding.tvTryMob.text = if (StringUtils.equals(
                type,
                "email"
            )
        ) getString(R.string.try_with_mob_number) else getString(R.string.try_with_email)

        _binding.pinview.addTextChangedListener {
            setPinViewLineColor(R.color.text_main)
            it?.toString()?.let { code ->
                _binding.tvResult.isVisible = false
                if (code.isEmpty() || code.length < 4) {
                } else {
                    if (code == "0000") {
                        _binding.pinview.clearFocus()
                        KeyboardUtils.hideKeyboard(this)
//                        showProgressBar()
                        viewModel.loginUser(phonenumber!!, "0000").observe(this) { task ->
//                            hideProgressBar()
                            if (task.responseCode == 200) {
                                dataKeyValue.save("idToken", task.body?.idToken ?: "reg")
                                dataKeyValue.save(
                                    "refreshToken",
                                    task.body?.refreshToken ?: "Constants.?"
                                )
                                saveUserData(task.body?.user)
                                showSuccess()
                                _binding.konfettiView.start(
                                    Presets.festive()
                                )
                                viewModel.compositeDisposable.add(FirebaseDBManager.instance.checkUserExist()
                                    .subscribe(
                                        {
                                            _binding.root.postDelayed({
                                                val intent = Intent(
                                                    this@VerifyOTPActivity,
                                                    HomeActivity::class.java
                                                )
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(intent)
                                                finishAffinity()
                                            }, 2600)
//                                            msg.showLongMsg("success-login")
                                        },
                                        { t ->
                                            Log.e(
                                                "FirebaseError",
                                                "authorizeAndRegisterUser: ",
                                                t
                                            )
                                            msg.showLongMsg("failed-authorizeAndRegisterUser - ${t.message}")
                                        }
                                    ))
                            } else {
                                showError()
                            }
                        }
                    } else {
//                        showProgressBar()
                        _binding.root.postDelayed({
                            showError()
//                            hideProgressBar()
                        }, 400)
                    }
                }
            }
        }
        //Back
        _binding.backBtn.setOnClickListener { onBackPressed() }
        _binding.tvTryMob.setOnClickListener { onBackPressed() }
    }

    private fun saveUserData(user: LoginResponse.User?) {
        dataKeyValue.save(SharedPrefsKey.USER_ROLE_TYPE, user?.roleType)
        dataKeyValue.save(SharedPrefsKey.USER_FIRST_NAME, user?.firstName)
        dataKeyValue.save(SharedPrefsKey.USER_LASTNAME, user?.lastName)
        dataKeyValue.save(SharedPrefsKey.USER_ID, user?.id ?: -1)
        dataKeyValue.save(SharedPrefsKey.USERNAME, user?.username)
        dataKeyValue.save(SharedPrefsKey.USER_CONTACT, user?.contactNumber)
        dataKeyValue.save(SharedPrefsKey.USER_EMAIL, user?.email)
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

    override fun onStop() {
        super.onStop()
        _binding.konfettiView.stopGracefully()
    }

}