package com.project.tex.aritist_profile.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.aritist_profile.model.UserProfileData
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityContactDetailsBinding

class ContactDetailsctivity : BaseActivity<ActivityContactDetailsBinding, ProfileViewModel>(),
    View.OnClickListener {

    override fun getViewBinding() = ActivityContactDetailsBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(ProfileViewModel::class.java)

    private var userData: UserProfileData.Body.Users.Body? = null
    private val activityResultUpdate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                it.data?.let {
                    val a = it.getStringExtra("mob")
                    val b = it.getStringExtra("email")
                    val c = it.getStringExtra("altmob")
                    val d = it.getStringExtra("altemail")
                    if (a != null && a.isNotEmpty()) {
                        userData?.contactNumber = a
                        _binding.mob.text = userData?.contactNumber ?: viewModel.getMobile() ?: "No Mobile Added "
                    }
                    if (b != null && b.isNotEmpty()) {
                        userData?.email = b
                        _binding.email.text = userData?.email ?: viewModel.getEmail() ?: "No Email Added"
                    }
                    if (c != null && c.isNotEmpty()) {
                        userData?.alternateContact = c
                        _binding.alternatemob.text =
                            userData?.alternateContact ?: getString(R.string.no_alternate_mob)
                    }
                    if (d != null && d.isNotEmpty()) {
                        userData?.alternateEmail = d
                        _binding.alternateemail.text =
                            userData?.alternateEmail ?: getString(R.string.no_alternate_email)
                    }
                }
                setResult(RESULT_OK, Intent().putExtra("data", userData))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            userData =
                intent?.getSerializableExtra("data", UserProfileData.Body.Users.Body::class.java)
        } else {
            userData = intent?.getSerializableExtra("data") as? UserProfileData.Body.Users.Body?
        }

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.mob.text = userData?.contactNumber ?: viewModel.getMobile() ?: "No Mobile Added "
        _binding.email.text = userData?.email ?: viewModel.getEmail() ?: "No Email Added"
        _binding.alternatemob.text =
            userData?.alternateContact ?: getString(R.string.no_alternate_mob)
        _binding.alternateemail.text =
            userData?.alternateEmail ?: getString(R.string.no_alternate_email)
        _binding.detailVisiblity.isChecked = (userData?.isVisible ?: 0) == 1
        _binding.detailVisiblity.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contact_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_profile_edit) {
            activityResultUpdate.launch(Intent(this, ContactEditActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val HELP: Int = 0
        const val ABOUT: Int = 1
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.detail_visiblity -> {
                _binding.detailVisiblity.isChecked = !_binding.detailVisiblity.isChecked
                viewModel.add(
                    viewModel.updateVisibility(if (_binding.detailVisiblity.isChecked) 1 else 0)
                        .subscribe(
                            {
                                if (it.responseCode == 200) {
                                    setResult(RESULT_OK)
                                    msg.showShortMsg("Contact visibility updated")
                                } else {
                                    msg.showShortMsg("Contact visibility failed to update")
                                }
                            },
                            {
                                _binding.detailVisiblity.isChecked =
                                    !_binding.detailVisiblity.isChecked
                                msg.showShortMsg(getString(R.string.something_wrong_try_again))
                            })
                )
            }
        }
    }
}