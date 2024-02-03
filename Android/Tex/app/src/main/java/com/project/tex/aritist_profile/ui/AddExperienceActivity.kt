package com.project.tex.aritist_profile.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.aritist_profile.model.ExperienceListResponse
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityAddExperienceBinding
import com.project.tex.main.ui.avid.avidCamera.view.NormalProgressDialog
import org.apache.commons.lang3.time.DateFormatUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddExperienceActivity : BaseActivity<ActivityAddExperienceBinding, ProfileViewModel>(),
    View.OnClickListener {

    private var startDate: Long? = null
    private var endDate: Long? = null
    override fun getViewBinding() = ActivityAddExperienceBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(ProfileViewModel::class.java)

    var ss: ExperienceListResponse.Body.Experience? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        ss = intent?.getSerializableExtra("data") as ExperienceListResponse.Body.Experience?
        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }

        ss?.let {
            _binding.edtCompanyName.setText(it.companyName)
            _binding.edtJobTitle.setText(it.jobTitle)
            _binding.mainToolbar.setTitle("Update Experience")
            _binding.btnAddExp.setText("Update Experience")
            try {
                val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                val formatter1: DateFormat = SimpleDateFormat("dd/MM/yyyy")

                val s1= formatter.parse(it.startDate)
                val s2= formatter.parse(it.endDate)
                val c= Calendar.getInstance()
                c.timeInMillis = s1.time
                val c2= Calendar.getInstance()
                c2.timeInMillis = s2.time
                startDate= c.timeInMillis
                endDate= c2.timeInMillis
                _binding.edtStartDatetime.setText(formatter1.format(s1))
                _binding.edtEndDatetime.setText(formatter1.format(s2))
            } catch (e: Exception) {

            }
            _binding.edtDescription.setText(it.description)
        }

        _binding.btnAddExp.setOnClickListener(this)

        _binding.edtStartDatetime.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    _binding.edtStartDatetime.text =
                        String.format("%02d/%02d/%02d", dayOfMonth, (monthOfYear + 1), year)
                    c[Calendar.YEAR] = year
                    c[Calendar.MONTH] = monthOfYear
                    c[Calendar.DAY_OF_MONTH] = dayOfMonth
                    startDate = c.timeInMillis
                },
                c[Calendar.YEAR],
                c[Calendar.MONTH],
                c[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = c.timeInMillis
            datePickerDialog.show()
        }
        _binding.edtEndDatetime.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    _binding.edtEndDatetime.text =
                        String.format("%02d/%02d/%02d", dayOfMonth, (monthOfYear + 1), year)
                    c[Calendar.YEAR] = year
                    c[Calendar.MONTH] = monthOfYear
                    c[Calendar.DAY_OF_MONTH] = dayOfMonth
                    endDate = c.timeInMillis
                },
                c[Calendar.YEAR],
                c[Calendar.MONTH],
                c[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = c.timeInMillis
            datePickerDialog.show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_exp -> {
                val startDateF = if (startDate != null) DateFormatUtils.format(
                    startDate!!,
                    "yyyy-MM-dd"
                ) else null
                val endDate =
                    if (endDate != null) DateFormatUtils.format(endDate!!, "yyyy-MM-dd") else null
                if (_binding.edtCompanyName.text?.isEmpty() == true) {
                    msg.showShortMsg(getString(R.string.enter_company_name))
                } else if (_binding.edtJobTitle.text?.isEmpty() == true) {
                    msg.showShortMsg(getString(R.string.enter_job_title))
                } else if (startDateF?.isEmpty() == true) {
                    msg.showShortMsg(getString(R.string.enter_start_date))
                } else if (endDate?.isEmpty() == true) {
                    msg.showShortMsg(getString(R.string.enter_end_date))
                } else if (_binding.edtDescription.text?.isEmpty() == true) {
                    msg.showShortMsg(getString(R.string.enter_description))
                } else {
                    NormalProgressDialog.showLoading(this, "Adding Experience", false)
                    viewModel.compositeDisposable.add(
                        if (ss == null) {
                            viewModel.createExperience(
                                _binding.edtCompanyName.text.toString(),
                                _binding.edtJobTitle.text.toString(),
                                startDateF!!, endDate!!,
                                _binding.edtDescription.text.toString(),
                            ).subscribe({
                                NormalProgressDialog.stopLoading(this)
                                if (it.responseCode == 200) {
                                    setResult(RESULT_OK)
                                    finish()
                                } else {
                                    msg.showShortMsg(getString(R.string.something_wrong_try_again))
                                }
                            }, {
                                NormalProgressDialog.stopLoading(this)
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            })
                        } else {
                            viewModel.updateExperience(
                                ss!!.id!!,
                                _binding.edtCompanyName.text.toString(),
                                _binding.edtJobTitle.text.toString(),
                                startDateF!!, endDate!!,
                                _binding.edtDescription.text.toString(),
                            ).subscribe({
                                NormalProgressDialog.stopLoading(this)
                                if (it.responseCode == 200) {
                                    setResult(RESULT_OK)
                                    finish()
                                } else {
                                    msg.showShortMsg(getString(R.string.something_wrong_try_again))
                                }
                            }, {
                                NormalProgressDialog.stopLoading(this)
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            })
                        }
                    )
                }
            }
        }
    }

}