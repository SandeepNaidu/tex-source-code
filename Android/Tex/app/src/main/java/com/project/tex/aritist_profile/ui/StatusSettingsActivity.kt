package com.project.tex.aritist_profile.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.aritist_profile.model.UserProfileData
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityStatusSettingBinding

class StatusSettingsActivity : BaseActivity<ActivityStatusSettingBinding, ProfileViewModel>() {

    override fun getViewBinding() = ActivityStatusSettingBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(ProfileViewModel::class.java)

    private var userData: UserProfileData.Body.Users.Body? = null
    private var onlineStatus = "AWAY"
    private var statusOfAvailability = "OPEN_TO_WORK"

    private var initCheck1 = 0
    private var initCheck2 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            userData =
                intent?.getSerializableExtra("data", UserProfileData.Body.Users.Body::class.java)
        } else {
            userData = intent?.getSerializableExtra("data") as? UserProfileData.Body.Users.Body?
        }

        val arrayStatusAvail = resources.getStringArray(R.array.availability)
        val arrayOnlineStatus = resources.getStringArray(R.array.status)

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }
        val adapterSpinner11 = ArrayAdapter.createFromResource(
            this,
            R.array.availability, R.layout.simple_spinner_item_for_event
        )
        adapterSpinner11.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_a)
        _binding.statusAvailTv.setAdapter(adapterSpinner11)

        val adapterSpinner = ArrayAdapter.createFromResource(
            this,
            R.array.status, R.layout.simple_spinner_item_for_event
        )
        adapterSpinner.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_a)
        _binding.onlineStatusTv.setAdapter(adapterSpinner)

        userData?.let {
            onlineStatus = it.onlineStatus ?: onlineStatus
            statusOfAvailability = it.statusOfAvailability ?: statusOfAvailability
            val a = arrayStatusAvail.indexOfFirst { i -> i.equals(it.statusOfAvailability) }
            val b = arrayOnlineStatus.indexOfFirst { i -> i.equals(it.onlineStatus) }
            _binding.statusAvailTv.setSelection(a)
            _binding.onlineStatusTv.setSelection(b)
        }

        _binding.onlineStatusTv.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (++initCheck1 > 1) {
                    onlineStatus = arrayOnlineStatus[position]
                    callUpdateApi(onlineStatus, statusOfAvailability)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        _binding.statusAvailTv.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (++initCheck2 > 1) {
                    statusOfAvailability = arrayStatusAvail[position]
                    callUpdateApi(onlineStatus, statusOfAvailability)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun callUpdateApi(onlineStatus: String, statusOfAvailability: String) {
        viewModel.updateStatusSetting(statusOfAvailability, onlineStatus).subscribe(
            {
                if (it.responseCode == 200) {
                    userData?.onlineStatus = onlineStatus
                    userData?.statusOfAvailability = statusOfAvailability
                    setResult(RESULT_OK, Intent().putExtra("data", userData))
                    msg.showShortMsg(getString(R.string.updated_successfully))
                }
            },
            {
                msg.showShortMsg(getString(R.string.something_wrong_try_again))
            })
    }

}