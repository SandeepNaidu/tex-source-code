package com.project.tex.welcome

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.project.tex.Check
import com.project.tex.NightMode
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivitySplashScreenBinding
import com.project.tex.main.HomeActivity
import com.project.tex.utils.IntentUtils

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity<ActivitySplashScreenBinding, SplashModel>() {
    private var sharedPref: NightMode? = null

    override fun getViewBinding(): ActivitySplashScreenBinding =
        ActivitySplashScreenBinding.inflate(layoutInflater)

    override fun getViewModelInstance(): SplashModel =
        ViewModelProvider(this).get(SplashModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = NightMode(this)
        if (sharedPref!!.loadNightModeState() == "night") {
            setTheme(R.style.SplashScreenDark)
        } else if (sharedPref!!.loadNightModeState() == "dim") {
            setTheme(R.style.SplashScreenDim)
        } else setTheme(R.style.SplashScreen)

        super.onCreate(savedInstanceState)

        val settings = getSharedPreferences("prefs", 0)
        val firstRun = settings.getBoolean("firstRun", false)
        if (!firstRun) {
            val editor = settings.edit()
            editor.putBoolean("firstRun", true)
            editor.apply()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(applicationContext, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(applicationContext, Check::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }

}