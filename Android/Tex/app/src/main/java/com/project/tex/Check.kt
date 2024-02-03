package com.project.tex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
import com.project.tex.aritist_profile.ui.PersonalInfoActivity
import com.project.tex.aritist_profile.ui.ProfileActivity
import com.project.tex.base.data.SharedPreference
import com.project.tex.db.table.AvidTakes
import com.project.tex.main.HomeActivity
import com.project.tex.main.ui.avid.avidCamera.ui.AvidCaptureActivity
import com.project.tex.main.ui.avid.avidCamera.ui.TakesActivity
import com.project.tex.onboarding.Constants.INTENT_URI
import com.project.tex.onboarding.login.QuickSignInActivity
import com.project.tex.utils.IntentUtils


class Check : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private val dataKeyValue: SharedPreference by lazy {
        SharedPreference(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        val isLoggedIn = dataKeyValue.getValueString("idToken")?.isNotEmpty() == true

        val accessToken = AccessToken.getCurrentAccessToken()
        val isFacebook = accessToken != null && !accessToken.isExpired

        if (currentUser == null && !isLoggedIn) {
            startActivity(Intent(this@Check, QuickSignInActivity::class.java))
        } else {
//            startActivity(Intent(this, PersonalInfoActivity::class.java))
            getDeeplinkIntent()
        }
        finish()
    }

    private fun getDeeplinkIntent() {
        IntentUtils.getDeeplinkIntent(intent).addOnSuccessListener(
            this
        ) { pendingDynamicLinkData -> // Get deep link from result (may be null if no link is found)
            if (pendingDynamicLinkData != null && pendingDynamicLinkData.link != null) {
                handleFirebaseLink(pendingDynamicLinkData.link)
            } else handleFirebaseLink(null)
        }.addOnFailureListener(
            this
        ) { handleFirebaseLink(null) }
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    Log.d("Check", "getDeeplinkIntent: ")
                    handleFirebaseLink(it.result.link)
                } else {
                    Log.e("Check", "getDeeplinkIntent: ", it.exception)
                    handleFirebaseLink(null)
                }
            }
    }

    private fun handleFirebaseLink(link: Uri?) {
        val startIntent = Intent(this@Check, HomeActivity::class.java)
//        val startIntent = Intent(this@Check, TakesActivity::class.java)
        if (link != null && !TextUtils.isEmpty(link.path)) {
            startIntent.putExtra(INTENT_URI, link.path)
        }
        startActivity(startIntent)
        finish()
    }
}