package com.project.tex.aritist_profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.project.tex.R
import com.project.tex.aritist_profile.model.GenericUpdateResponse
import com.project.tex.aritist_profile.otp_verify.VerifyContactOTPActivity
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityContactEditBinding
import com.project.tex.main.ui.avid.avidCamera.view.NormalProgressDialog
import com.project.tex.utils.ViewUtils.setBoxStrokeColorSelector
import io.reactivex.Single

class ContactEditActivity : BaseActivity<ActivityContactEditBinding, ProfileViewModel>(),
    View.OnClickListener {
    private val CLEAR_TEXT: Int = 2
    private val NONE: Int = -1
    private var endIconState: Int = NONE
    private val ERROR: Int = 1
    private val TYPO: Int = 3
    private val SUCCESS: Int = 0

    private var isMobVerifiedNumber: String? = null
    private var isEmailVerified: String? = null
    private var mob = ""
    private var email = ""
    private var altMob = ""
    private var altEmail = ""

    override fun getViewBinding() = ActivityContactEditBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(ProfileViewModel::class.java)

    private val activityResultUpdate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                when (type) {
                    0 -> {
                        isMobVerifiedNumber = mob
                        msg.showShortMsg("Mobile number verified Successfully")
                        updateBtn()
                    }
                    1 -> {
                        isEmailVerified = email
                        msg.showShortMsg("Email ID verified Successfully")
                        updateBtn()
                    }
                }
            }
        }

    private var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.edtMobile.doAfterTextChanged {
            mob = it.toString()
            if (it?.length == 10) {
                setSuccess(_binding.clearIdMob, _binding.mobileTil)
                type = 0
                activityResultUpdate.launch(
                    Intent(this, VerifyContactOTPActivity::class.java).putExtra(
                        "id", it.toString()
                    )
                )
            } else {
                hideEndIcon(_binding.clearIdMob, _binding.mobileTil)
            }
            updateBtn()
            if (it?.isEmpty() == true) {
                hideEndIcon(_binding.clearIdMob, _binding.mobileTil)
            }
        }

        _binding.edtEmail.doAfterTextChanged {
            email = it.toString()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                setSuccess(_binding.clearIdEmail, _binding.emailTil)
                type = 1
                activityResultUpdate.launch(
                    Intent(this, VerifyContactOTPActivity::class.java).putExtra(
                        "id", it.toString()
                    )
                )
            } else {
                hideEndIcon(_binding.clearIdEmail, _binding.emailTil)
            }
            updateBtn()
            if (it?.isEmpty() == true) {
                hideEndIcon(_binding.clearIdMob, _binding.mobileTil)
            }
        }

        _binding.edtAlternativeMob.doAfterTextChanged {
            altMob = it.toString()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                setSuccess(_binding.clearIdAltMob, _binding.alternativeMobTil)
                type = 2
            } else {
                hideEndIcon(_binding.clearIdAltMob, _binding.alternativeMobTil)
            }
            updateBtn()
            if (it?.isEmpty() == true) {
                hideEndIcon(_binding.clearIdAltMob, _binding.alternativeMobTil)
            }
        }

        _binding.edtAlternativeEmail.doAfterTextChanged {
            altEmail = it.toString()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                setSuccess(_binding.clearIdAltEmail, _binding.alternativeEmailTil)
                type = 3
            } else {
                hideEndIcon(_binding.clearIdAltEmail, _binding.alternativeEmailTil)
            }
            updateBtn()
            if (it?.isEmpty() == true) {
                hideEndIcon(_binding.clearIdAltEmail, _binding.alternativeEmailTil)
            }
        }

        _binding.btnUpdate.setOnClickListener(this)
    }

    fun updateBtn() {
        var isValid = true
        if (mob.isNotEmpty() && isMobVerifiedNumber != mob) {
            isValid = false
        }
        if (email.isNotEmpty() && isEmailVerified != email) {
            isValid = false
        }
        if (altMob.isNotEmpty() && altMob.length != 10) {
            isValid = false
        }
        if (altEmail.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(altEmail)
                .matches()
        ) {
            isValid = false
        }
        if (mob.isEmpty() && email.isEmpty() && altMob.isEmpty() && altEmail.isEmpty()) {
            isValid = false
        }

        _binding.btnUpdate.isEnabled = isValid
    }

    private fun setSuccess(clearId: ImageView, userIdTil: TextInputLayout) {
        endIconState = SUCCESS
        updateEndIcon(clearId, userIdTil)
    }

    private fun setError(clearId: ImageView, userIdTil: TextInputLayout) {
        endIconState = ERROR
        updateEndIcon(clearId, userIdTil)
    }

    private fun hideEndIcon(clearId: ImageView, userIdTil: TextInputLayout) {
        endIconState = NONE
        updateEndIcon(clearId, userIdTil)
    }

    private fun updateEndIcon(clearId: ImageView, userIdTil: TextInputLayout) {
        when (endIconState) {
            ERROR -> {
                clearId.isVisible = true
                clearId.setImageResource(R.drawable.ic_baseline_error_24)
                userIdTil.setBoxStrokeColorSelector(
                    ContextCompat.getColor(this, R.color.error),
                    ContextCompat.getColor(this, R.color.error)
                )
            }
            SUCCESS -> {
                clearId.isVisible = true
                clearId.setImageResource(R.drawable.ic_done)
                userIdTil.setBoxStrokeColorSelector(
                    ContextCompat.getColor(this, R.color.text_underline_success),
                    ContextCompat.getColor(this, R.color.text_underline_success)
                )
            }
            NONE, CLEAR_TEXT -> {
                clearId.isVisible = false
                userIdTil.setBoxStrokeColorSelector(
                    ContextCompat.getColor(this, R.color.colorPrimary),
                    ContextCompat.getColor(this, R.color.text_hint)
                )
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_update -> {
                NormalProgressDialog.showLoading(this, "Updating Details")
                val a1 = if (mob.isNotEmpty() && isMobVerifiedNumber == mob) {
                    viewModel.updateContact(mob)
                } else {
                    Single.just(GenericUpdateResponse(responseCode = 400))
                }
                val a2 = if (email.isNotEmpty() && isEmailVerified == email) {
                    viewModel.updateEmail(email)
                } else {
                    Single.just(GenericUpdateResponse(responseCode = 400))
                }
                val a3 = if (altMob.isNotEmpty() && altMob.length == 10) {
                    viewModel.updateAltMob(altMob)
                } else {
                    Single.just(GenericUpdateResponse(responseCode = 400))
                }
                val a4 = if (altEmail.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        altEmail
                    ).matches()
                ) {
                    viewModel.updateAltEmail(altEmail)
                } else {
                    Single.just(GenericUpdateResponse(responseCode = 400))
                }

                viewModel.add(Single.zip(a1.onErrorReturn { GenericUpdateResponse(responseCode = 400) },
                    a2.onErrorReturn { GenericUpdateResponse(responseCode = 400) },
                    a3.onErrorReturn { GenericUpdateResponse(responseCode = 400) },
                    a4.onErrorReturn { GenericUpdateResponse(responseCode = 400) }) { t1, t2, t3, t4 ->
                    listOf<Int?>(
                        t1.responseCode,
                        t2.responseCode,
                        t3.responseCode,
                        t4.responseCode
                    )
                }
                    .subscribe({
                        NormalProgressDialog.stopLoading(this)
                        msg.showShortMsg("Updated Successfully")
                        setResult(RESULT_OK, Intent().apply {
                            viewModel.updateContactLastUpdate()
                            var i = 0
                            it.forEach {
                                if (it != null && it == 200) {
                                    if (i == 0) {
                                        putExtra("mob", mob)
                                    }
                                    if (i == 1) {
                                        putExtra("email", email)
                                    }
                                    if (i == 2) {
                                        putExtra("altmob", altMob)
                                    }
                                    if (i == 3) {
                                        putExtra("altemail", altEmail)
                                    }
                                }
                                i++
                            }
                        })
                        finish()
                    }, {
                        NormalProgressDialog.stopLoading(this)
                        msg.showShortMsg(getString(R.string.update_failed))
                    })
                )
            }
        }
    }
}