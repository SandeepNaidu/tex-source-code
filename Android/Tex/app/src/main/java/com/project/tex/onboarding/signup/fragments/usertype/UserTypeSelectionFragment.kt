package com.project.tex.onboarding.signup.fragments.usertype

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentUserTypeSelectionBinding
import com.project.tex.onboarding.signup.SignUpActivity
import com.project.tex.onboarding.signup.SignupViewModel

class UserTypeSelectionFragment : BaseFragment<FragmentUserTypeSelectionBinding>(),
    View.OnClickListener {

    companion object {
        fun newInstance() = UserTypeSelectionFragment()
    }

    private lateinit var viewModel: UserTypeSelectionViewModel
    private val mainViewModel by activityViewModels<SignupViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserTypeSelectionViewModel::class.java)

        setListener()
        (activity as SignUpActivity).showIndicator(true, 0)
    }

    private fun setListener() {
        _binding.cardArtist.setOnClickListener(this)
        _binding.cardRecruiter.setOnClickListener(this)
        _binding.tvLogin.setOnClickListener(this)
        _binding.backBtn.setOnClickListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as SignUpActivity).showIndicator(true, 0)
    }

    private fun openNextFragment(usertype: String) {
        mainViewModel.setUsertype(usertype)
        (activity as SignUpActivity).openNameFragment()
    }

    override fun getViewBinding() = FragmentUserTypeSelectionBinding.inflate(layoutInflater)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.card_artist -> {
                openNextFragment("Artist")
            }
            R.id.tv_login -> {
                activity?.finish()
            }
            R.id.back_btn -> {
                activity?.onBackPressed()
            }
            R.id.card_recruiter -> {
                openNextFragment("Recruiter")
            }
        }
    }
}