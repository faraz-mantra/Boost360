package com.boost.presignin.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.boost.presignin.R
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentLoginBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.login.UserProfileVerificationRequest
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.ui.mobileVerification.AuthBaseFragment
import com.boost.presignin.ui.mobileVerification.FP_LIST_FRAGMENT
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.observeOnce
import com.framework.extensions.onTextChanged
import com.framework.pref.clientId
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*
import android.widget.Toast

import android.view.View.OnFocusChangeListener
import androidx.lifecycle.LiveData
import com.boost.presignin.BuildConfig
import com.boost.presignin.model.login.VerificationRequestResultV3
import com.framework.pref.APPLICATION_JIO_ID
import com.framework.base.BaseResponse
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : AuthBaseFragment<FragmentLoginBinding>() {

  private var resultLogin: VerificationRequestResult? = null
  private var safeNetClient: SafetyNetClient? = null
  private val googleRecaptchakey = "6LfoGxQmAAAAAPjCeVIg4fqdVerlsUGYpbi7vNsi" //nowfloats

  private val job = CoroutineScope(Dispatchers.Main).launch {
    delay(60_000) // delay 1 minute
    binding.verificationSuccessLayout.visibility = View.GONE
    binding.verifyBt.visibility = View.VISIBLE
    binding.loginBt.isEnabled = false
    Toast.makeText(baseActivity, "Verification from Captcha expired. Please complete it again!", Toast.LENGTH_SHORT).show()
  }

  companion object {
    @JvmStatic
    fun newInstance() = LoginFragment().apply {}
  }

  override fun getLayout(): Int {
    return R.layout.fragment_login
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun resultLogin(): VerificationRequestResult? {
    return resultLogin
  }

  override fun authTokenData(): AuthTokenDataItem? {
    return if (resultLogin()?.authTokens.isNullOrEmpty().not()
    ) resultLogin()?.authTokens!![0] else null
  }

  override fun onResume() {
    super.onResume()
    binding?.usernameEt?.post { baseActivity.showKeyBoard(binding?.usernameEt) }
  }

  override fun onCreateView() {
    super.onCreateView()
    safeNetClient = SafetyNet.getClient(context!!)
    WebEngageController.trackEvent(PS_LOGIN_USERNAME_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.usernameEt?.onTextChanged { onDataChanged() }
    binding?.passEt?.onTextChanged { onDataChanged() }
    setOnClickListener(binding?.forgotTv, binding?.loginBt, binding?.loginWithNumberBtn, binding?.helpTv,binding?.verifyBt)
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }
    binding?.passEt?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
      binding?.forgotTv?.setTextColor(if (hasFocus) getColor(R.color.colorAccentLight) else getColor(R.color.black_4a4a4a))
    }
  }

  private fun goBack() {
    baseActivity.finish()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.forgotTv -> {
        WebEngageController.trackEvent(PS_LOGIN_FORGOT_PASSWORD_CLICK, CLICK, NO_EVENT_VALUE)
        navigator?.startActivity(
          LoginActivity::class.java,
          Bundle().apply { putInt(FRAGMENT_TYPE, FORGOT_FRAGMENT) })
      }
      binding?.loginBt -> {
        WebEngageController.trackEvent(PS_LOGIN_FORGOT_PASSWORD_CLICK, CLICK, NO_EVENT_VALUE)
        loginApiVerify(
          binding?.usernameEt?.text?.toString()?.trim(),
          binding?.passEt?.text?.toString()?.trim()
        )
      }
      binding?.loginWithNumberBtn -> {
        baseActivity.setResult(AppCompatActivity.RESULT_OK, Intent())
        baseActivity.finish()
      }
      binding?.helpTv -> {
        needHelp()
      }
      binding?.verifyBt -> {
        verifyWithCaptcha()
      }
    }
  }

  private fun loginApiVerify(userName: String?, password: String?) {
    showProgress()

    var verifyUsernamePassword: LiveData<BaseResponse>? = null
    if (BuildConfig.FLAVOR.equals("partone") || BuildConfig.FLAVOR.equals("jioonline")) {
      verifyUsernamePassword = viewModel?.verifyUserProfile(UserProfileVerificationRequest(loginKey = userName, loginSecret = password, clientId = clientId))
    }else{
      verifyUsernamePassword = viewModel?.verifyUserProfileVertical(UserProfileVerificationRequest(loginKey = userName, loginSecret = password, clientId = clientId))
    }

    verifyUsernamePassword?.observeOnce(viewLifecycleOwner) {
      hideProgress()
      try {
        val response: VerificationRequestResult? =
          if (BuildConfig.FLAVOR.equals("partone") || BuildConfig.FLAVOR.equals("jioonline")) (it as? VerificationRequestResult) else (it as? VerificationRequestResultV3)!!.result
        if(response!=null) {
          if ((BuildConfig.FLAVOR.equals("partone") || BuildConfig.FLAVOR.equals("jioonline")).not()) {
            //set status from statusCode
            response.status = (it as? VerificationRequestResultV3)!!.StatusCode
          }
          if (response?.isSuccess() == true && response.loginId.isNullOrEmpty()
              .not() && response.authTokens.isNullOrEmpty().not()
          ) {
//        if(BuildConfig.FLAVOR.equals("boosthealth"))
            storeUserDetail(response)
          } else {
            showShortToast(getString(R.string.ensure_that_the_entered_username_and_password_))
          }
        }else{
          showShortToast(getString(R.string.ensure_that_the_entered_username_and_password_))
        }
      } catch (e: Exception){
        e.printStackTrace()
      }
    }
  }

  private fun storeUserDetail(response: VerificationRequestResult) {
    if (baseActivity.packageName.equals(APPLICATION_JIO_ID, ignoreCase = true)) {
      this.resultLogin = response
      authTokenData()?.createAccessTokenAuth()
    } else {
      if (response.authTokens!!.size == 1) {
        this.resultLogin = response
        authTokenData()?.createAccessTokenAuth()
      } else {
        navigator?.startActivity(MobileVerificationActivity::class.java, Bundle().apply {
          putInt(FRAGMENT_TYPE, FP_LIST_FRAGMENT);putSerializable(IntentConstant.EXTRA_FP_LIST_AUTH.name, response)
        })
      }
    }
  }

  private fun onDataChanged() {
    val username = binding?.usernameEt?.text?.toString()
    val password = binding?.passEt?.text?.toString()
    binding?.loginBt?.isEnabled = !username.isNullOrBlank() && !password.isNullOrBlank()
  }

  private fun verifyWithCaptcha() {
    safeNetClient?.verifyWithRecaptcha(googleRecaptchakey)
      ?.addOnSuccessListener { response ->
        val userResponseToken = response.tokenResult
        if (userResponseToken?.isNotEmpty() == true) {
          Toast.makeText(baseActivity, "Validity of the verification will be valid for 1 minute.", Toast.LENGTH_SHORT).show()
          binding.verificationSuccessLayout.visibility = View.VISIBLE
          binding.verifyBt.visibility = View.GONE
          binding.loginBt.isEnabled = true
          enableValidatedView()
        }
      }
      ?.addOnFailureListener { e ->
        Toast.makeText(context!!, "Verification Failed", Toast.LENGTH_SHORT).show()
        if (e is ApiException) {
          Log.d("MainAct", "Error: e.statusCode}")
        } else {
          Log.d("MainAct", "Error: ${e.message}")
        }
      }
  }

  private fun enableValidatedView() {
    job.start()
  }
}