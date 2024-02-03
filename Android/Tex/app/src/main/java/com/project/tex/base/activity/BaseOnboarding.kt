package com.project.tex.base.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.project.tex.R
import org.json.JSONException
import org.json.JSONObject
import java.util.*

abstract class BaseOnboarding<VB : ViewBinding, VM : ViewModel> : BaseActivity<VB, VM>() {

    val mAuth: FirebaseAuth by lazy {
        return@lazy FirebaseAuth.getInstance()
    }

    val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create();
    }

    private lateinit var mSocialLoginCallback: SocialLoginCallback

    fun fbInit() {
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    setFacebookData(loginResult)
                }

                override fun onCancel() {
                    // App code
                    mSocialLoginCallback.error(0)
                }

                override fun onError(exception: FacebookException) {
                    // App code
                    mSocialLoginCallback.error(1, exception)
                }
            })
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(loginResult.accessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
                    // Application code
                    try {
                        Log.i("Response", response.toString())
                        val email = response?.getJSONObject()!!.getString("email")
                        val firstName = response?.getJSONObject()!!.getString("first_name")
                        val lastName = response?.getJSONObject()!!.getString("last_name")
                        mSocialLoginCallback.success(loginResult, email)

                    } catch (e: JSONException) {
                        msg.showShortMsg(getString(R.string.something_wrong_try_again))
                    }
                }

            })
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name")
        request.parameters = parameters
        request.executeAsync()
    }

    fun login(socialLoginCallback: SocialLoginCallback) {
//        if (mSocialLoginCallback::isInitialized())
        mSocialLoginCallback = socialLoginCallback
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"));
    }

    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initGoogleLogin()

        fbInit()
    }

    fun initGoogleLogin(): GoogleSignInClient {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        return mGoogleSignInClient
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        initGoogleLogin()
    }

    interface SocialLoginCallback {
        fun success(data: LoginResult, email: String)
        fun error(code: Int, error: Exception? = null)
    }
}