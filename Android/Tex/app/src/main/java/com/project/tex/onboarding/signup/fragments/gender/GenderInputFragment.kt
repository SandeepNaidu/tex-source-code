package com.project.tex.onboarding.signup.fragments.gender

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView
import com.project.tex.R
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentGenderInputBinding
import com.project.tex.onboarding.signup.SignUpActivity
import com.project.tex.onboarding.signup.SignupViewModel
import com.project.tex.utils.ViewUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateFormatUtils

class GenderInputFragment : BaseFragment<FragmentGenderInputBinding>(), View.OnClickListener {

    companion object {
        fun newInstance() = GenderInputFragment()
    }

    private val MALE: String = "MALE"
    private val FEMALE: String = "FEMALE"
    private val OTHER: String = "OTHER"
    private lateinit var viewModel: GenderInputViewModel
    private val mainViewModel: SignupViewModel by viewModels({ requireActivity() })

    private var selectedGender = MALE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(GenderInputViewModel::class.java)

        (activity as SignUpActivity).showIndicator(true, 4)

        _binding.btnNext.setOnClickListener(this)
        _binding.textDoitLater.setOnClickListener(this)
        _binding.male.setOnClickListener(this)
        _binding.female.setOnClickListener(this)
        _binding.other.setOnClickListener(this)
        _binding.backBtn.setOnClickListener(this)

        msg.updateSnackbarView(view)
        setBoxOutline(_binding.cardMale)
        enableBtn(true)

        observe()

        _binding.edtId.addTextChangedListener {
            it?.let { t ->
                if (t.isNotEmpty()) {
                    setBoxOutline(_binding.cardOther)
                    enableBtn(true)
                    _binding.userIdTil.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                } else {
                    _binding.userIdTil.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.text_hint)
                }
            }
        }
    }

    private fun observe() {
        viewModel.updateUserLD.observe(viewLifecycleOwner) { response ->
            if (response.responseCode == 200) {
                (activity as SignUpActivity).openHome()
            } else {
                msg.showLongMsg(getString(R.string.something_went_wrong))
            }
        }
    }

    private fun enableBtn(isValid: Boolean) {
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

    private fun openNextFragment() {
        openHome()
    }

    private fun openHome() {
        (activity as SignUpActivity).openHome()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.male -> {
                if (selectedGender == MALE) return
                mainViewModel.setGender("Male")
                selectedGender = MALE
                clearStroke()
                setBoxOutline(v.parent as MaterialCardView)
                _binding.userIdTil.isVisible = false
                _binding.otherTv.isVisible = true
                _binding.edtId.text?.clear()
                enableBtn(true)
            }
            R.id.female -> {
                if (selectedGender == FEMALE) return
                selectedGender = FEMALE
                clearStroke()
                setBoxOutline(v.parent as MaterialCardView)
                mainViewModel.setGender("Female")
                _binding.userIdTil.isVisible = false
                _binding.otherTv.isVisible = true
                _binding.edtId.text?.clear()
                enableBtn(true)
            }
            R.id.other -> {
                if (selectedGender == OTHER) return
                selectedGender = OTHER
                clearStroke()
                _binding.userIdTil.isVisible = true
                _binding.otherTv.isVisible = false
                enableBtn(false)
            }
            R.id.btn_next -> {
                if (selectedGender == OTHER) {
                    mainViewModel.setGender(_binding.edtId.text.toString())
                }
                dataKeyStore.getValueString("idToken")?.let {
                    mainViewModel.userIdLD.value?.let { uid ->
                        val name = StringUtils.split(mainViewModel.userNameLD.value, " ")
                        val fName =
                            if (name.isNotEmpty()) name[0] else mainViewModel.userNameLD.value
                        val lName = if (name.size > 1) name.reduceIndexed { index, acc, s ->
                            if (index == 1) {
                                return@reduceIndexed s
                            }
                            return@reduceIndexed "${acc} ${s}"
                        } else " "
                        viewModel.updateUser(
                            it,
                            dob = mainViewModel.dobLD.value,
                            age = mainViewModel.ageLD.value,
                            username = uid,
                            contactNumber = if (mainViewModel.isUserIdEmailTypeLD.value == false) uid else null,
                            firstName = fName,
                            lastName = lName,
                            gender = if (selectedGender == OTHER) _binding.edtId.text.toString() else selectedGender
                        )
                    }
                } ?: kotlin.run {
                    msg.showShortMsg("token is null!")
                }
            }
            R.id.text_doit_later -> {
                openHome()
            }
            R.id.back_btn -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun clearStroke() {
        _binding.cardFemale.strokeWidth = 0
        _binding.cardMale.strokeWidth = 0
        _binding.cardOther.strokeWidth = 0
    }

    private fun setBoxOutline(card: MaterialCardView) {
        card.strokeWidth = ViewUtils.dpToPx(requireContext(), 2)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as SignUpActivity).showIndicator(true, 4)
    }

    override fun getViewBinding() = FragmentGenderInputBinding.inflate(layoutInflater)

}