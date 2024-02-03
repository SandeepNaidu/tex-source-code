package com.project.tex.onboarding.signup.fragments.contactver

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseOnboarding
import com.project.tex.base.data.SharedPrefsKey
import com.project.tex.base.fragment.BaseLoginFragment
import com.project.tex.databinding.FragmentContactVerifyBinding
import com.project.tex.onboarding.Constants
import com.project.tex.onboarding.dialog.RegisterErrorDialog
import com.project.tex.onboarding.model.ErrorValidation
import com.project.tex.onboarding.model.LoginResponse
import com.project.tex.onboarding.signup.SignUpActivity
import com.project.tex.onboarding.signup.SignupViewModel
import com.project.tex.utils.AnimUtils.smoothUpateTextSize
import com.project.tex.utils.KeyboardUtils
import com.project.tex.utils.ViewUtils.setBoxStrokeColorSelector
import org.apache.commons.lang3.StringUtils

class ContactVerifyFragment : BaseLoginFragment<FragmentContactVerifyBinding>(),
    View.OnClickListener,
    View.OnFocusChangeListener, BaseOnboarding.SocialLoginCallback {

    companion object {
        fun newInstance() = ContactVerifyFragment()
    }

    private val regErrorDialog by lazy {
        RegisterErrorDialog()
    }

    private val mainViewModel: SignupViewModel by viewModels({ requireActivity() })

    private val TAG: String? = ContactVerifyFragment::class.simpleName
    private lateinit var viewModel: ContactVerifyViewModel
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun getViewBinding() = FragmentContactVerifyBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactVerifyViewModel::class.java)

        (activity as SignUpActivity).showIndicator(true, 2)

        mGoogleSignInClient = activity.initGoogleLogin()

        _binding.tvTitle.setText(mainViewModel.userNameLD.value ?: "")
        _binding.btnNext.setOnClickListener(this)
        _binding.googleSignin.setOnClickListener(this)
        _binding.facebookLogin.setOnClickListener(this)
        _binding.tvLogin.setOnClickListener(this)
        _binding.backBtn.setOnClickListener(this)
        _binding.edtId.onFocusChangeListener = this
        viewModel.subscribeSubject()
        observeLiveData()
        _binding.edtId.addTextChangedListener { s ->
            setButtonEnable(false)
            _binding.clearId.isVisible = false
            viewModel.setUserId(s.toString())
            s?.toString()?.let { name ->
                if (name.isNotEmpty())
                    _binding.edtId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                viewModel.emit()
                if (!isValid()) {
                    setNormal()
                }
                showHideFieldError(ErrorValidation(true, viewModel.getInputType(s.toString())))

            }
        }
    }

    private fun setButtonEnable(isValid: Boolean) {
        _binding.btnNext.isEnabled = isValid
        val color = ContextCompat.getColor(
            requireContext(),
            R.color.text_light
        )
        _binding.btnText.setTextColor(if (isValid) Color.WHITE else color)
        TextViewCompat.setCompoundDrawableTintList(
            _binding.btnText,
            if (isValid) ColorStateList.valueOf(Color.WHITE) else ColorStateList.valueOf(
                color
            )
        )
    }

    private fun observeLiveData() {
        viewModel.usernameAvailableLD.observe(viewLifecycleOwner) { response ->
            KeyboardUtils.hideKeyboard(getActivity())
            response?.let {
                if (response.responseCode == 200) {
                    if (response.body?.isAvailable?.body?.isAvailable == true) {
                        setSuccess()
                        setButtonEnable(true)
                    } else {
                        setError()
                        setButtonEnable(false)
                        activity.mGoogleSignInClient.signOut()
                        showError()
                    }
                } else {
                    activity.mGoogleSignInClient.signOut()
                    showError()
                }
            }
        }
        viewModel.validationErrorLD.observe(viewLifecycleOwner) { isValid ->
            if (!_binding.edtId.text.toString().isEmpty())
                showHideFieldError(isValid)
        }
    }

    private fun showError() {
        _binding.root.postDelayed( {
            regErrorDialog.show(childFragmentManager, null)
        }, 300)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
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

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account == null) {
            // user not signed in
            _binding.googleSignin.visibility = View.VISIBLE
        } else {
            // already signed in goto main
//            msg.showLongMsg(account.email.toString())

            account.idToken?.let {
//                _binding.googleSignin.visibility = View.GONE
                firebaseAuthWithGoogle(it)
            } ?: kotlin.run {
//                (activity as SignUpActivity).openOtpVerifyFragment()
            }
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        (activity as BaseOnboarding<*, *>).mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                requireActivity()
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = (activity as BaseOnboarding<*, *>).mAuth.currentUser
                    user?.email?.let { mainViewModel.setUserId(it) }
//                    (activity as SignUpActivity).openOtpVerifyFragment()
                    validateCred(mainViewModel.userIdLD.value)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "    signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        updateUI(account)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as SignUpActivity).showIndicator(true, 2)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                if (isValid()) {
                    if (_binding.progressBar.isAnimating) return
                    _binding.progressBar.visibility = View.VISIBLE
                    _binding.progressBar.playAnimation()
                    _binding.btnText.visibility = View.INVISIBLE
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            _binding.btnText.visibility = View.VISIBLE
                            _binding.progressBar.visibility = View.INVISIBLE
                            _binding.progressBar.pauseAnimation()
                            setSuccess()
                            openNext()
                        }, 2000
                    )
                }
            }
            R.id.google_signin -> {
                signIn()
            }
            R.id.facebook_login -> {
                activity.login(this)
            }
            R.id.back_btn -> {
                activity?.onBackPressed()
            }
            R.id.tv_login -> {
                activity.mAuth.signOut()
                mGoogleSignInClient.signOut()
                activity?.finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activity?.callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setSuccess() {
        _binding.clearId.setImageResource(R.drawable.ic_done)
        _binding.clearId.isVisible = true
        _binding.userIdTil.setBoxStrokeColorSelector(
            ContextCompat.getColor(requireContext(), R.color.text_underline_success),
            ContextCompat.getColor(requireContext(), R.color.text_underline_success)
        )
    }

    private fun setError() {
        _binding.clearId.isVisible = true
        _binding.clearId.setImageResource(R.drawable.ic_baseline_error_24)
        _binding.userIdTil.setBoxStrokeColorSelector(
            ContextCompat.getColor(requireContext(), R.color.error),
            ContextCompat.getColor(requireContext(), R.color.error)
        )
    }

    private fun setNormal() {
        _binding.clearId.isVisible = false
        _binding.clearId.setImageResource(0)
        _binding.userIdTil.setBoxStrokeColorSelector(
            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
            ContextCompat.getColor(requireContext(), R.color.text_hint)
        )
    }

    private fun setTypoTextIcon() {
        _binding.clearId.isVisible = true
        _binding.clearId.setImageResource(R.drawable.ic_close_red)
        _binding.userIdTil.setBoxStrokeColorSelector(
            ContextCompat.getColor(requireContext(), R.color.text_alert_red),
            ContextCompat.getColor(requireContext(), R.color.text_alert_red)
        )
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
            setNormal()
        } else {
            setTypoTextIcon()
        }
    }

    private fun isValid(): Boolean {
        val s = _binding.edtId.text.toString()
        var isValid = false
        if (android.util.Patterns.PHONE.matcher(s).matches() && s.length == 10) {
            isValid = true
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            isValid = true
        }

        return isValid
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            _binding.edtId.smoothUpateTextSize(16, 24)
        } else {
//            _binding.userId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
    }

    private fun openNext() {
        mainViewModel.setUserId(_binding.edtId.text.toString())
        (activity as SignUpActivity).openOtpVerifyFragment()
    }

    override fun success(data: LoginResult, email: String) {
        (data as? LoginResult)?.let {
            dataKeyStore.save("sidToken", it.accessToken.token)
//            dataKeyStore.save("idToken", it.accessToken)
            mainViewModel.setUserId(email)
        }

        validateCred(email)
    }

    private fun validateCred(email: String?) {
        if (email != null) {
            viewModel.checkSocialUserExist(email)?.observe(this) {
                if (it.responseCode == 200 && it.body?.isAvailable?.body?.isAvailable == true) {
                    registerNGotoDobPage()
                } else {
                    msg.showLongMsg(getString(R.string.username_not_available))
                    activity.mAuth.signOut()
                    _binding.googleSignin.visibility = View.VISIBLE
                    _binding.facebookLogin.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun registerNGotoDobPage() {
        val isMobile = android.util.Patterns.PHONE.matcher(mainViewModel.userIdLD.value!!)
            .matches() && mainViewModel.userIdLD.value!!.length == 10

        mainViewModel.setIsUserIdEmailType(!isMobile)
        viewModel.register(
            mainViewModel.userIdLD.value!!,
            mainViewModel.userNameLD.value!!,
            if (StringUtils.equals(
                    mainViewModel.userTypeLD.value,
                    "Artist"
                )
            ) 121 else 122,
            mainViewModel.isUserIdEmailTypeLD.value!!
        )?.observe(viewLifecycleOwner) {
            if (it.responseCode == 200) {
                viewModel.loginUser(mainViewModel.userIdLD.value!!, "0000").observe(this) {
                    if (it.responseCode == 200) {
                        dataKeyStore.save("idToken", it.body?.idToken ?: "reg")
                        dataKeyStore.save("refreshToken", it.body?.refreshToken ?: "Constants.TOKEN_")
                        saveUserData(it.body?.user)
                        (activity as SignUpActivity).openDobFragment()
                    } else {
                        msg.showLongMsg(getString(R.string.something_wrong_try_again))
                        _binding.googleSignin.visibility = View.VISIBLE
                    }
                }
            } else {
                activity.mAuth.signOut()
                msg.showLongMsg(getString(R.string.something_wrong_try_again))
            }
        }
    }

    private fun saveUserData(user: LoginResponse.User?) {
        dataKeyStore.save(SharedPrefsKey.USER_ROLE_TYPE, user?.roleType)
        dataKeyStore.save(SharedPrefsKey.USER_FIRST_NAME, user?.firstName)
        dataKeyStore.save(SharedPrefsKey.USER_LASTNAME, user?.lastName)
        dataKeyStore.save(SharedPrefsKey.USER_ID, user?.id ?: -1)
        dataKeyStore.save(SharedPrefsKey.USERNAME, user?.username)
        dataKeyStore.save(SharedPrefsKey.USER_CONTACT, user?.contactNumber)
        dataKeyStore.save(SharedPrefsKey.USER_EMAIL, user?.email)
    }

    override fun error(code: Int, error: Exception?) {
        msg.showLongMsg(getString(R.string.something_went_wrong))
    }
}