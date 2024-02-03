package com.project.tex.onboarding.signup.fragments.basic

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentEnterNameBinding
import com.project.tex.onboarding.signup.SignUpActivity
import com.project.tex.onboarding.signup.SignupViewModel
import com.project.tex.utils.AnimUtils.smoothUpateTextSize

class EnterNameFragment :
    BaseFragment<FragmentEnterNameBinding>(),
    View.OnClickListener, View.OnFocusChangeListener {

    companion object {
        fun newInstance() = EnterNameFragment()
    }

    private lateinit var viewModel: EnterNameViewModel
    private val mainViewModel: SignupViewModel by viewModels({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(EnterNameViewModel::class.java)

        (activity as SignUpActivity).showIndicator(true, 1)

        _binding.btnNext.setOnClickListener(this)
        _binding.backBtn.setOnClickListener(this)
        _binding.tvLogin.setOnClickListener(this)
        _binding.edtName.setOnFocusChangeListener(this)
        _binding.edtName.addTextChangedListener {
            it?.toString()?.let { name ->
                val isValid = name.length > 3
                _binding.btnNext.isEnabled = isValid
                val color = ContextCompat.getColor(
                    requireContext(),
                    R.color.text_light
                )
                _binding.btnText.setTextColor(if (isValid) Color.WHITE else color)
                TextViewCompat.setCompoundDrawableTintList(
                    _binding.btnText,
                    if (isValid) ColorStateList.valueOf(Color.WHITE) else ColorStateList.valueOf(color)
                )
            }
        }
    }

    private fun openNextFragment() {
        mainViewModel.setUserName(_binding.edtName.text.toString())
        (activity as SignUpActivity).openContactVerification()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                if (_binding.edtName.text.toString().length > 3) {
                    openNextFragment()
                } else {
                    msg.showLongMsg("Enter Full Name")
                }
            }
            R.id.back_btn -> {
                activity?.onBackPressed()
            }
            R.id.tv_login -> {
                activity?.finish()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as SignUpActivity).showIndicator(true, 1)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            _binding.edtName.smoothUpateTextSize(16, 24)
        } else {
//            _binding.userId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
    }

    override fun getViewBinding() = FragmentEnterNameBinding.inflate(layoutInflater)
}