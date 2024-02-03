package com.project.tex.settings.viewmodel

import com.project.tex.R

data class SettingItem(
    val itemId: Int,
    val title: String,
    val startIcon: Int,
    val endIcon: Int = R.drawable.right_arrow
)
