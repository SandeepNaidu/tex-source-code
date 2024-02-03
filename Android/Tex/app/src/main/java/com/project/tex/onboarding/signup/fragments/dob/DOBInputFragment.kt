package com.project.tex.onboarding.signup.fragments.dob

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.calendar.CalendarActivity
import com.project.tex.databinding.FragmentDOBInputBinding
import com.project.tex.onboarding.signup.SignUpActivity
import com.project.tex.onboarding.signup.SignupViewModel
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateFormatUtils
import java.text.SimpleDateFormat
import java.util.*


class DOBInputFragment : BaseFragment<FragmentDOBInputBinding>(), View.OnClickListener {

    companion object {
        fun newInstance() = DOBInputFragment()
    }

    private var age: Int = 1
    private var date: Long = 0L
    private lateinit var viewModel: DOBInputViewModel
    private val mainViewModel: SignupViewModel by viewModels({ requireActivity() })
    private val c = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DOBInputViewModel::class.java)

        msg.updateSnackbarView(view)
        _binding.backBtn.setOnClickListener(this)
        _binding.btnNext.setOnClickListener(this)
        _binding.textDoitLater.setOnClickListener(this)
        _binding.edtId.setOnClickListener(this)

        (activity as SignUpActivity).showIndicator(true, 3)

        observe()
    }

    private fun observe() {
        viewModel.registerLD.observe(viewLifecycleOwner) { response ->
            if (response.responseCode == 200) {
                (activity as SignUpActivity).openGenderFragment()
            } else {
                msg.showLongMsg(getString(R.string.something_went_wrong))
            }
        }
    }

    private fun openHome() {
        (activity as SignUpActivity).openHome()
    }

    private fun calculateAge(birthdate: Date?): Int {
        val birth = Calendar.getInstance()
        birth.time = birthdate
        val today = Calendar.getInstance()
        var yearDifference = (today[Calendar.YEAR]
                - birth[Calendar.YEAR])
        if (today[Calendar.MONTH] < birth[Calendar.MONTH]) {
            yearDifference--
        } else {
            if (today[Calendar.MONTH] === birth[Calendar.MONTH]
                && today[Calendar.DAY_OF_MONTH] < birth[Calendar.DAY_OF_MONTH]
            ) {
                yearDifference--
            }
        }
        return yearDifference
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as SignUpActivity).showIndicator(true, 3)
    }

    private val mStartForResult = registerForActivityResult(StartActivityForResult(),
        ActivityResultCallback<ActivityResult>() { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    date = data.getLongExtra("date", 0L)
                    if (date != 0L) {
                        // Display Selected date in textbox
                        _binding.edtId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                        _binding.edtId.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.text_main
                            )
                        )
                        _binding.edtId.typeface =
                            (ResourcesCompat.getFont(requireContext(), R.font.gilroy_bold))
                        val c1 = Calendar.getInstance()
                        c1.timeInMillis = date
                        age = calculateAge(c1.time)
                        _binding.edtId.setText(
                            "${c1.get(Calendar.DAY_OF_MONTH)} ${
                                SimpleDateFormat(
                                    "MMMM"
                                ).format(c1.getTime())
                            } ${c1.get(Calendar.YEAR)}"
                        )
                        _binding.tvAge.text = "$age"
                        c.time = c1.time
                        _binding.cardAge.isVisible = true
                        _binding.btnNext.isEnabled = true
                        _binding.btnText.setTextColor(Color.WHITE)
                        TextViewCompat.setCompoundDrawableTintList(
                            _binding.btnText,
                            ColorStateList.valueOf(Color.WHITE)
                        )
                    }
                }
            }
        })

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edt_id -> {
                mStartForResult.launch(Intent(context, CalendarActivity::class.java))
            }
            R.id.btn_next -> {
                if (_binding.edtId.text?.isNotEmpty() == true) {
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
                            mainViewModel.setDob(DateFormatUtils.format(date, "yyyy-MM-dd"))
                            mainViewModel.setAge(age)
                            viewModel.updateUser(
                                it,
                                dob = DateFormatUtils.format(date, "yyyy-MM-dd"),
                                age = age,
                                username = uid,
                                contactNumber = if (mainViewModel.isUserIdEmailTypeLD.value == false) uid else null,
                                firstName = fName,
                                lastName = lName
                            )
                        }
                    } ?: kotlin.run {
                        msg.showShortMsg("Token is null!")
                    }
                } else {
                    msg.showLongMsg("Please select DOB!")
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

    override fun getViewBinding() = FragmentDOBInputBinding.inflate(layoutInflater)

}