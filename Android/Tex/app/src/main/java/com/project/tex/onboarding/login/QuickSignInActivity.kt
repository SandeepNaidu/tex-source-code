package com.project.tex.onboarding.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseOnboarding
import com.project.tex.base.data.SharedPrefsKey
import com.project.tex.databinding.ActivityQuickSigninBinding
import com.project.tex.main.HomeActivity
import com.project.tex.onboarding.dialog.LoginErrorDialog
import com.project.tex.onboarding.model.ErrorValidation
import com.project.tex.onboarding.model.LoginResponse
import com.project.tex.onboarding.otp.VerifyOTPActivity
import com.project.tex.onboarding.signup.SignUpActivity
import com.project.tex.utils.AnimUtils.smoothUpateTextSize
import com.project.tex.utils.KeyboardUtils
import com.project.tex.utils.ViewUtils.setBoxStrokeColorSelector
import org.apache.commons.lang3.StringUtils


class QuickSignInActivity : BaseOnboarding<ActivityQuickSigninBinding, QuickSigninViewModel>(),
    View.OnClickListener, View.OnFocusChangeListener, BaseOnboarding.SocialLoginCallback {
    private val CLEAR_TEXT: Int = 2
    private val NONE: Int = -1
    private var endIconState: Int = NONE
    private val ERROR: Int = 1
    private val TYPO: Int = 3
    private val SUCCESS: Int = 0
    private val TAG: String = QuickSignInActivity::class.java.simpleName

    override fun getViewBinding() = ActivityQuickSigninBinding.inflate(layoutInflater)
    private val loginErrorDialog by lazy {
        LoginErrorDialog()
    }

    override fun getViewModelInstance() =
        ViewModelProvider(this).get(QuickSigninViewModel::class.java)

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (result.resultCode == RESULT_OK) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginManager.getInstance().logOut()
        mGoogleSignInClient.revokeAccess()
        dataKeyValue.clearSharedPreference()

        msg.updateSnackbarView(_binding.root)
        _binding.userId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.setUserId(s.toString())
                showHideFieldError(ErrorValidation(true, viewModel.getInputType(s.toString())))
                setBtnEnable(false)
                if (StringUtils.isEmpty(s.toString())) {
                    hideEndIcon()
                } else {
                    setClearTextIcon()
                    viewModel.emit()
                }
            }
        })
        viewModel.subscribeSubject()
        _binding.login.setOnClickListener(this)
        _binding.clearId.setOnClickListener(this)
        _binding.googleSignin.setOnClickListener(this)
        _binding.facebookLogin.setOnClickListener(this)
        _binding.tvNewAccount.setOnClickListener(this)
        _binding.userId.setOnFocusChangeListener(this)
        _binding.backBtn.setOnClickListener(this)
        observeLiveData()
    }

    private fun setBtnEnable(isValid: Boolean) {
        _binding.login.isEnabled = isValid
        if (isValid) {
            _binding.btnText.setTextColor(Color.WHITE)
            TextViewCompat.setCompoundDrawableTintList(
                _binding.btnText, ColorStateList.valueOf(Color.WHITE)
            )
            viewModel.emit()
        } else {
            val color = ContextCompat.getColor(
                this@QuickSignInActivity, R.color.text_light
            )
            TextViewCompat.setCompoundDrawableTintList(
                _binding.btnText, ColorStateList.valueOf(color)
            )
            _binding.btnText.setTextColor(color)
        }
    }

    private fun observeLiveData() {
        viewModel.usernameAvailableLD.observe(this) { response ->
            response?.let {
                if (response.responseCode == 200) {
                    KeyboardUtils.hideKeyboard(this)
                    if (response.body?.isAvailable?.body?.isAvailable == true) {
                        setError()
                        showErrorDialog()
                        mGoogleSignInClient.signOut()
                        setBtnEnable(false)
                    } else {
                        setSuccess()
                        setBtnEnable(true)
                    }
                } else {
                    msg.showLongMsg(getString(R.string.something_wrong_try_again))
                    setClearTextIcon()
                }
            }
        }

        viewModel.validationErrorLD.observe(this) { isValid ->
            if (!_binding.userId.text.toString().isEmpty())
                showHideFieldError(isValid)
        }
    }

    private fun showHideFieldError(valid: ErrorValidation) {
        val msg = when (valid.type) {
            0 -> getString(R.string.email_typo_error)
            1 -> getString(R.string.mobile_typo_error)
            else -> null
        }
        _binding.tvError.text = if (valid.isValid) null else msg
        _binding.tvError.isVisible = !valid.isValid
        if (valid.isValid) {
            hideEndIcon()
        } else {
            setTypoTextIcon()
        }
    }

    private fun hideEndIcon() {
        endIconState = NONE
        updateEndIcon()
    }

    private fun setClearTextIcon() {
        endIconState = CLEAR_TEXT
        updateEndIcon()
    }

    private fun setTypoTextIcon() {
        endIconState = TYPO
        updateEndIcon()
    }

    private fun updateEndIcon() {
        when (endIconState) {
            TYPO -> {
                _binding.clearId.isVisible = true
                _binding.clearId.setImageResource(R.drawable.ic_close_red)
                _binding.userIdTil.setBoxStrokeColorSelector(
                    ContextCompat.getColor(this, R.color.text_alert_red),
                    ContextCompat.getColor(this, R.color.text_alert_red)
                )
            }
            ERROR -> {
                _binding.clearId.isVisible = true
                _binding.clearId.setImageResource(R.drawable.ic_baseline_error_24)
                _binding.userIdTil.setBoxStrokeColorSelector(
                    ContextCompat.getColor(this, R.color.error),
                    ContextCompat.getColor(this, R.color.error)
                )
            }
            SUCCESS -> {
                _binding.clearId.isVisible = true
                _binding.clearId.setImageResource(R.drawable.ic_done)
                _binding.userIdTil.setBoxStrokeColorSelector(
                    ContextCompat.getColor(this, R.color.text_underline_success),
                    ContextCompat.getColor(this, R.color.text_underline_success)
                )
            }
            NONE, CLEAR_TEXT -> {
                _binding.clearId.isVisible = false
                _binding.userIdTil.setBoxStrokeColorSelector(
                    ContextCompat.getColor(this, R.color.colorPrimary),
                    ContextCompat.getColor(this, R.color.text_hint)
                )
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account == null) {
            // user not signed in
            _binding.googleSignin.visibility = View.VISIBLE
        } else {
            // already signed in goto main
            account.idToken?.let {
                _binding.googleSignin.visibility = View.GONE
                firebaseAuthWithGoogle(it)
            } ?: kotlin.run {
//                openHome()
            }
        }
    }

    private fun updateUI2(account: FirebaseUser?) {
        if (account == null) {
            // user not signed in
            _binding.googleSignin.visibility = View.VISIBLE
        } else {
            _binding.googleSignin.visibility = View.GONE
            account.email?.let { email ->
                validateCred(email)
            }
        }
    }

    private fun validateCred(email: String) {
        viewModel.checkSocialUserExist(email)?.observe(this) {
            KeyboardUtils.hideKeyboard(this)
            if (it.responseCode == 200 && it.body?.isAvailable?.body?.isAvailable == false) {
                viewModel.loginUser(email, "0000").observe(this) {
                    if (it.responseCode == 200) {
//                        if(StringUtils.isEmpty(it.body?.refreshToken)){
//                            msg.showLongMsg(getString(R.string.something_wrong_try_again))
//                            return@observe
//                        }
                        saveUserData(it.body?.user)
                        dataKeyValue.save("idToken", it.body?.idToken ?: "reg")
                        dataKeyValue.save("refreshToken", it.body?.refreshToken ?: "TOKEN_")

                        openHome()
                    } else {
                        msg.showLongMsg(getString(R.string.something_wrong_try_again))
                        _binding.googleSignin.visibility = View.VISIBLE
                    }
                }
            } else {
                mGoogleSignInClient.signOut()
                showErrorDialog()
                _binding.googleSignin.visibility = View.VISIBLE
            }
        }
    }

    private fun saveUserData(user: LoginResponse.User?) {
        dataKeyValue.save(SharedPrefsKey.USER_ROLE_TYPE, user?.roleType)
        dataKeyValue.save(SharedPrefsKey.USER_FIRST_NAME, user?.firstName)
        dataKeyValue.save(SharedPrefsKey.USER_LASTNAME, user?.lastName)
        dataKeyValue.save(SharedPrefsKey.USER_ID, user?.id ?: -1)
        dataKeyValue.save(SharedPrefsKey.USERNAME, user?.username)
        dataKeyValue.save(SharedPrefsKey.USER_CONTACT, user?.contactNumber)
        dataKeyValue.save(SharedPrefsKey.USER_EMAIL, user?.email)
    }

    private fun showErrorDialog() {
        _binding.root.postDelayed({
            loginErrorDialog.showDialog(supportFragmentManager)
        }, 300)
    }

    private fun openHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth.currentUser
                updateUI2(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                updateUI2(null)
            }
        }
    }

    fun clearText() {
        _binding.userId.setText("")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_btn -> {
                onBackPressed()
            }
            R.id.tv_new_account -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
            R.id.google_signin -> {
                signIn()
            }
            R.id.facebook_login -> {
                login(this)
            }
            R.id.instagram_login -> {
            }
            R.id.login -> {
                if (_binding.progressBar.isAnimating) return
                val userId: String = _binding.userId.getText().toString().trim { it <= ' ' }
                if (userId.isEmpty()) {
                    Snackbar.make(v, "Enter your credentials!", Snackbar.LENGTH_LONG).show()
                    _binding.progressBar.visibility = View.INVISIBLE
                    _binding.btnText.visibility = View.INVISIBLE
                } else {
                    _binding.progressBar.visibility = View.VISIBLE
                    _binding.progressBar.playAnimation()
                    _binding.btnText.visibility = View.INVISIBLE
                    KeyboardUtils.hideKeyboard(this)

//                    viewModel.login()
                    viewModel.loginUser(userId, "0000").observe(this) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                openVerifyOtp(it.body?.user)
                            }, 1000
                        )
                    }
                }
            }
            R.id.clear_id -> {
                if (endIconState == CLEAR_TEXT) _binding.userId.setText("")
            }
        }
    }

    private fun openVerifyOtp(user: LoginResponse.User?) {
        val intent = Intent(this@QuickSignInActivity, VerifyOTPActivity::class.java)
        intent.putExtra("user_input", viewModel.userIdLD.value)
        user?.let {
            intent.putExtra("name", "${user.firstName} ${user.lastName}")
            val isMobile = android.util.Patterns.PHONE.matcher(viewModel.userIdLD.value)
                .matches() && viewModel.userIdLD.value?.length == 10
            intent.putExtra("idType", if (isMobile) "mobile" else "email")
        }

        _binding.btnText.visibility = View.VISIBLE
        _binding.progressBar.visibility = View.INVISIBLE
        _binding.progressBar.pauseAnimation()
        setSuccess()
        _binding.userId.clearFocus()
        startActivity(intent)
    }

    private fun setSuccess() {
        endIconState = SUCCESS
        updateEndIcon()
    }

    private fun setError() {
        endIconState = ERROR
        updateEndIcon()
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            _binding.userId.smoothUpateTextSize(16, 24)
        } else {
//            _binding.userId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
    }

    override fun onDestroy() {
        _binding.progressBar.cancelAnimation()
        super.onDestroy()
    }

    override fun success(data: LoginResult, email: String) {
        (data).let {
            dataKeyValue.save("sidToken", it.accessToken.token)
            validateCred(email)
        }
    }

    override fun error(code: Int, error: Exception?) {
        msg.showShortMsg(getString(R.string.something_wrong_try_again))
    }

}