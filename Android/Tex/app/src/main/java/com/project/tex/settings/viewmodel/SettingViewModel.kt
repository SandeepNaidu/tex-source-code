package com.project.tex.settings.viewmodel

import com.project.tex.R
import com.project.tex.base.BaseViewModel
import com.project.tex.base.data.SharedPrefsKey
import com.project.tex.onboarding.OnboardRepository

class SettingViewModel() : BaseViewModel() {
    private val userRepository: OnboardRepository by lazy {
        OnboardRepository
    }

    public fun getSettingPageItem(): List<SettingItem> {
        return listOf(
            SettingItem(0, "Invite Friends", R.drawable.ic_invite_friend),
            SettingItem(1, "Notifications", R.drawable.ic_notification_setting),
            SettingItem(2, "Privacy", R.drawable.ic_privacy_setting),
            SettingItem(3, "Security", R.drawable.ic_security_setting),
            SettingItem(4, "Help", R.drawable.ic_help_setting),
            SettingItem(5, "About", R.drawable.ic_about_setting),
        )
    }

    public fun getInviteFriendPageItem(): List<SettingItem> {
        return listOf(
            SettingItem(0, "Invite Friends by WhatsApp", 0, 0),
            SettingItem(1, "Invite Friends by SMS", 0, 0),
            SettingItem(2, "Invite Friends by Email", 0, 0),
            SettingItem(3, "Invite Friends by Link", 0, 0),
            SettingItem(4, "Invite Friends by Contacts", 0),
        )
    }

    public fun getFaqs(): List<FaqItem> {
        val s =
            "The big benefit to tagging people and pages on social media is that you’re instantly connected to these users and companies. Your update links directly to their account, and the users and companies get a notification that they’ve been mentioned."
        return listOf(
            FaqItem(0, "Best practices for tagging people and pages", s),
            FaqItem(1, "How to get your customers involved and engaged", s),
            FaqItem(2, "Can you carry over a social strategy from one channel to the next?", s),
            FaqItem(3, "Top 5 reasons why social media is worth the investment", s),
            FaqItem(4, "Can you carry over a social strategy from one channel to the next?", s),
        )
    }


    fun getIsCompleteNotificationPaused(): Boolean {
        return userRepository.dataKeyValue.getValueBoolean(
            SharedPrefsKey.IS_NOTIFICATION_PAUSE,
            false
        )
    }

    fun getIsEnabledSecurity(): Boolean {
        return userRepository.dataKeyValue.getValueBoolean(
            SharedPrefsKey.IS_SECURITY_ENABLED,
            true
        )
    }

    fun setIsEnabledSecurity(checked: Boolean) {
        userRepository.dataKeyValue.save(
            SharedPrefsKey.IS_SECURITY_ENABLED,
            checked
        )
    }

    fun setIsCompleteNotificationPaused(data: Boolean) {
        return userRepository.dataKeyValue.save(
            SharedPrefsKey.IS_NOTIFICATION_PAUSE,
            data
        )
    }


    fun getEmailNotificationPaused(): Boolean {
        return userRepository.dataKeyValue.getValueBoolean(
            SharedPrefsKey.IS_EMAIL_NOTIFICATION_PAUSE,
            false
        )
    }

    fun setEmailNotificationPaused(data: Boolean) {
        return userRepository.dataKeyValue.save(
            SharedPrefsKey.IS_EMAIL_NOTIFICATION_PAUSE,
            data
        )
    }

    fun getSMSNotificationPaused(): Boolean {
        return userRepository.dataKeyValue.getValueBoolean(
            SharedPrefsKey.IS_SMS_NOTIFICATION_PAUSE,
            false
        )
    }

    fun setSMSNotificationPaused(data: Boolean) {
        return userRepository.dataKeyValue.save(
            SharedPrefsKey.IS_SMS_NOTIFICATION_PAUSE,
            data
        )
    }
}