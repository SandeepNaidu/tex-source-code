package com.project.tex.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivitySettingsBinding
import com.project.tex.settings.adapter.SettingAdapter
import com.project.tex.settings.viewmodel.SettingViewModel
import com.project.tex.utils.IntentUtils


class InviteFriendsActivity : BaseActivity<ActivitySettingsBinding, SettingViewModel>(),
    SettingAdapter.OnClickListener {
    val msgs =
        "Hey there, I am inviting you to install tex app and here is the link - https://play.google.com/store/apps/details?id="

    override fun getViewBinding() = ActivitySettingsBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(SettingViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.mainToolbar.setTitle(R.string.invite_friends)

        _binding.settingRv.adapter = SettingAdapter(viewModel.getInviteFriendPageItem(), this)
    }

    override fun itemClicked(pos: Int, id: Int) {
        when (id) {
            INVITE_FRIEND_WHATSAPP -> {
                IntentUtils.shareTextToWhatsapp(
                    this,
                    msgs + packageName,
                    "Whatsapp not installed on your device"
                )
            }
            INVITE_FRIEND_SMS -> {
                IntentUtils.shareTextToSms(
                    this, msgs + packageName,
                    "No sms app was installed on your device"
                )
            }
            INVITE_FRIEND_EMAIL -> {
                IntentUtils.shareEmail(
                    this,
                    msgs+packageName,
                    "No email app was installed on your device"
                )
            }
            INVITE_FRIEND_LINK -> {

            }
            INVITE_FRIEND_CONTACTS -> {

            }
        }
    }

    companion object {
        const val INVITE_FRIEND_WHATSAPP: Int = 0
        const val INVITE_FRIEND_SMS: Int = 1
        const val INVITE_FRIEND_EMAIL: Int = 2
        const val INVITE_FRIEND_LINK: Int = 3
        const val INVITE_FRIEND_CONTACTS: Int = 4
    }

}