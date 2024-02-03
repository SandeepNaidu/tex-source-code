package com.project.tex

import android.content.Context
import android.content.SharedPreferences

class NightMode(context: Context) {
    val sharedPreferences: SharedPreferences
    fun setNightModeState(state: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("NightMode", state)
        editor.apply()
    }

    fun loadNightModeState(): String? {
        return sharedPreferences.getString("NightMode", "day")
    }

    init {
        sharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE)
    }
}