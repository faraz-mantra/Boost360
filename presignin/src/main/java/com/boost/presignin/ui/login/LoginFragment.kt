package com.boost.presignin.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.framework.utils.ValidationUtils
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*

class LoginFragment : AuthBaseFragment<FragmentLoginBinding>() {

  private var resultLogin: VerificationRequestResult? = null

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
    binding?.usernameEt?.post{ baseActivity.showKeyBoard(binding?.usernameEt) }
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PS_LOGIN_USERNAME_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.usernameEt?.onTextChanged { onDataChanged() }
    binding?.passEt?.onTextChanged { onDataChanged() }
    setOnClickListener(
      binding?.forgotTv,
      binding?.loginBt,
      binding?.loginWithNumberBtn,
      binding?.helpTv
    )
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }
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
    }
  }

  private fun loginApiVerify(userName: String?, password: String?) {
    showProgress()
    viewModel?.verifyUserProfile(
      UserProfileVerificationRequest(
        loginKey = userName,
        loginSecret = password,
        clientId = clientId
      )
    )?.observeOnce(viewLifecycleOwner, {
      val response = it as? VerificationRequestResult
      if (response?.isSuccess() == true && response.loginId.isNullOrEmpty()
          .not() && response.authTokens.isNullOrEmpty().not()
      ) {
        storeUserDetail(response)
      } else {
        hideProgress()
        showShortToast(getString(R.string.ensure_that_the_entered_username_and_password_))
      }
    })
  }

  private fun storeUserDetail(response: VerificationRequestResult) {
    hideProgress()
//    if (response.profileProperties?.userMobile.isNullOrEmpty().not() && ValidationUtils.isMobileNumberValid(response.profileProperties?.userMobile!!)) {
//      navigator?.startActivity(LoginActivity::class.java, Bundle().apply {
//        putInt(FRAGMENT_TYPE, LOGIN_SUCCESS_FRAGMENT);putSerializable(IntentConstant.EXTRA_FP_LIST_AUTH.name, response)
//      })
//    } else {
    if (response.authTokens!!.size == 1) {
      this.resultLogin = response
      authTokenData()?.createAccessTokenAuth()
    } else {
      navigator?.startActivity(MobileVerificationActivity::class.java, Bundle().apply {
        putInt(
          FRAGMENT_TYPE,
          FP_LIST_FRAGMENT
        );putSerializable(IntentConstant.EXTRA_FP_LIST_AUTH.name, response)
      })
    }
//    }
  }

  private fun onDataChanged() {
    val username = binding?.usernameEt?.text?.toString()
    val password = binding?.passEt?.text?.toString()
    binding?.loginBt?.isEnabled = !username.isNullOrBlank() && !password.isNullOrBlank()
  }
}