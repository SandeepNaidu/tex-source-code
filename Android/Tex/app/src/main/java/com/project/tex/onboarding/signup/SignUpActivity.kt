package com.project.tex.onboarding.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseOnboarding
import com.project.tex.databinding.ActivitySignUpBinding
import com.project.tex.main.HomeActivity
import com.project.tex.onboarding.signup.fragments.basic.EnterNameFragment
import com.project.tex.onboarding.signup.fragments.contactver.ContactVerifyFragment
import com.project.tex.onboarding.signup.fragments.dob.DOBInputFragment
import com.project.tex.onboarding.signup.fragments.gender.GenderInputFragment
import com.project.tex.onboarding.signup.fragments.otp.OtpVerificationFragment
import com.project.tex.onboarding.signup.fragments.usertype.UserTypeSelectionFragment

class SignUpActivity : BaseOnboarding<ActivitySignUpBinding, SignupViewModel>() {
    override fun getViewBinding() = ActivitySignUpBinding.inflate(layoutInflater)

    override fun getViewModelInstance() = ViewModelProvider(this).get(SignupViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGoogleSignInClient.signOut()
        mAuth.signOut()
        supportFragmentManager.beginTransaction()
            .add(_binding.container.id, UserTypeSelectionFragment.newInstance())
            .addToBackStack(null)
            .commit()

    }

    fun openNameFragment() {
        supportFragmentManager.beginTransaction()
            .replace(_binding.container.id, EnterNameFragment.newInstance())
            .addToBackStack("name")
            .commitAllowingStateLoss()
    }

    fun openContactVerification() {
        supportFragmentManager.beginTransaction()
            .replace(_binding.container.id, ContactVerifyFragment.newInstance())
            .addToBackStack("cv")
            .commitAllowingStateLoss()
    }

    fun openOtpVerifyFragment() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in_1, R.anim.fade_out_2, R.anim.fade_in_1, R.anim.fade_out_2)
            .replace(
                _binding.container.id,
                OtpVerificationFragment.newInstance(
                    viewModel.userIdLD.value ?: "",
                    viewModel.userTypeLD.value ?: "Artist"
                )
            )
            .addToBackStack("otp")
            .commitAllowingStateLoss()
    }

    fun openGenderFragment() {
        supportFragmentManager.beginTransaction()
            .replace(_binding.container.id, GenderInputFragment.newInstance())
            .addToBackStack("gender")
            .commitAllowingStateLoss()
    }

    fun openDobFragment() {
        supportFragmentManager.popBackStackImmediate()
        supportFragmentManager.beginTransaction()
            .replace(_binding.container.id, DOBInputFragment.newInstance())
            .addToBackStack("dob")
            .commitAllowingStateLoss()
    }

    fun showIndicator(show: Boolean, pos: Int) {
        _binding.dotsIndicator.visibility = if (show) View.VISIBLE else View.INVISIBLE
        _binding.dotsIndicator.setDotSelection(pos)
        _binding.dotsIndicator.setOnTouchListener { v, event -> true }
    }

    fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finishAffinity()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(_binding.container.id) is UserTypeSelectionFragment) {
            finish()
        } else if (supportFragmentManager.findFragmentById(_binding.container.id) is DOBInputFragment) {
            openHome()
        } else if (supportFragmentManager.findFragmentById(_binding.container.id) is GenderInputFragment) {
            openHome()
        } else if (supportFragmentManager.findFragmentById(_binding.container.id) is ContactVerifyFragment) {
            mAuth.signOut()
            mGoogleSignInClient.signOut()
            super.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

}