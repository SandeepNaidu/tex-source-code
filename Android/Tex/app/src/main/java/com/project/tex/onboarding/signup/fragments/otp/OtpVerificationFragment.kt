package com.project.tex.onboarding.signup.fragments.otp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentOtpVerificationBinding
import com.project.tex.firebase.FirebaseDBManager
import com.project.tex.onboarding.Constants
import com.project.tex.onboarding.Presets
import com.project.tex.onboarding.signup.SignUpActivity
import com.project.tex.onboarding.signup.SignupViewModel
import com.project.tex.utils.KeyboardUtils
import org.apache.commons.lang3.StringUtils

class OtpVerificationFragment : BaseFragment<FragmentOtpVerificationBinding>(),
    View.OnClickListener {

    companion object {
        fun newInstance(userId: String, userType: String) = OtpVerificationFragment().apply {
            arguments = Bundle().also {
                it.putString("user_input", userId)
                it.putString("userType", userType)
            }
        }
    }

    private var userId: String? = null

    //    private var timer: CountDownTimer? = null
    private val TAG: String? = OtpVerificationFragment::class.simpleName

    private lateinit var viewModel: OtpVerificationViewModel
    private val mainViewModel: SignupViewModel by viewModels({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(OtpVerificationViewModel::class.java)
        (activity as SignUpActivity).showIndicator(false, 2)

        _binding.pinview.addTextChangedListener {
            it?.toString()?.let { code ->
                _binding.tvResult.isVisible = false
                if (code.isEmpty() || code.length < 4) {
                } else {
//                    requireContext().showProgressBar()
                    if (code.equals("0000")) {
                        _binding.close.isEnabled = false
                        viewModel.register(
                            mainViewModel.userIdLD.value!!,
                            mainViewModel.userNameLD.value!!,
                            if (StringUtils.equals(
                                    mainViewModel.userTypeLD.value,
                                    "Artist"
                                )
                            ) 121 else 122,
                            mainViewModel.isUserIdEmailTypeLD.value!!
                        )
                    } else {
                        _binding.root.postDelayed({
                            showError()
//                            hideProgressBar()
                        }, 400)
                    }
                }
            }
        }
        userId = arguments?.getString("user_input")
        val userType: String? = arguments?.getString("userType")
        _binding.userType.text = userType
        _binding.userAddress.text = userId
        _binding.close.setOnClickListener(this)

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.registerLD.observe(viewLifecycleOwner) { response ->
//            hideProgressBar()
            KeyboardUtils.hideKeyboard(activity)
            response?.let {
                if (response.responseCode == 200) {
                    userId?.let { it1 ->
                        viewModel.loginUser(it1, "0000").observe(viewLifecycleOwner) { login ->
                            if (login.responseCode == 200) {
                                showSuccess()
                                dataKeyStore.save("idToken", login.body?.idToken)
                                dataKeyStore.save("refreshToken", login.body?.refreshToken?: "Constants.TOKEN_")
                                dataKeyStore.save("userFirstName", login.body?.user?.firstName)
                                dataKeyStore.save("userLastName", login.body?.user?.lastName)
                                login.body?.user?.id?.let { it2 ->
                                    dataKeyStore.save("userId",
                                        it2
                                    )
                                }
                                dataKeyStore.save(
                                    "userType", if (StringUtils.equals(
                                            mainViewModel.userTypeLD.value,
                                            "Artist"
                                        )
                                    ) "artistadmin" else "recruiteradmin"
                                )
                                val isMobile = android.util.Patterns.PHONE.matcher(it1)
                                    .matches() && it1.length == 10

                                _binding.konfettiView.start(
                                    Presets.festive()
                                )
                                mainViewModel.setIsUserIdEmailType(!isMobile)
                                viewModel.compositeDisposable.add(
                                    FirebaseDBManager.instance.authorizeAndRegisterUser(
                                        mainViewModel.userNameLD.value!!,
                                        mainViewModel.userIdLD.value!!,
                                        mainViewModel.isUserIdEmailTypeLD.value!!,
                                        mainViewModel.userTypeLD.value!!
                                    ).subscribe(
                                        {
                                            _binding.root.postDelayed({
                                                openNextFragment()
                                            }, 1200)
                                        }, { t ->
                                            _binding.close.isEnabled = true
                                            Log.e("FirebaseError", "authorizeAndRegisterUser: ", t)
//                                            msg.showLongMsg("failed-authorizeAndRegisterUser - ${t.message}")
                                        })
                                )
                            } else {
                                _binding.close.isEnabled = true
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            }
                        }
                    }
                } else {
                    _binding.close.isEnabled = true
                    showError()
                }
            }
        }
    }

    private fun showSuccess() {
        _binding.pinview.setLineColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_underline_success
            )
        )
        _binding.tvResult.text = getString(R.string.verification_success)
        _binding.tvResult.isVisible = true
        _binding.tvDidntReceiveCode.isVisible = false
        _binding.tvTimer.isVisible = false
        _binding.tvResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_underline_success))
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            _binding.tvResult,
            R.drawable.ic_done,
            0,
            0,
            0
        )
    }

    private fun showError() {
        _binding.tvResult.text = getString(R.string.text_verification_failed_try_again)
        _binding.tvResult.isVisible = true
        _binding.tvResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_alert_red))
        _binding.pinview.setLineColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_alert_red
            )
        )
        _binding.pinview.setText("")
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            _binding.tvResult,
            R.drawable.ic_close_red,
            0,
            0,
            0
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.close -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun openNextFragment() {
        (activity as? SignUpActivity)?.openDobFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as? SignUpActivity)?.showIndicator(false, 2)
    }

    override fun onStop() {
        super.onStop()
        _binding.konfettiView.stopGracefully()
    }

    override fun getViewBinding() = FragmentOtpVerificationBinding.inflate(layoutInflater)
}