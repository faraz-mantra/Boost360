package com.boost.presignin.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentLoginBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.login.UserProfileVerificationRequest
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.service.APIService
import com.boost.presignin.ui.mobileVerification.FloatingPointAuthFragment
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import com.framework.extensions.onTextChanged
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.webengageconstant.*
import java.util.*

class LoginFragment : AppBaseFragment<FragmentLoginBinding, LoginSignUpViewModel>() {

  private var session: UserSessionManager? = null

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

  override fun onCreateView() {
    WebEngageController.trackEvent(PS_LOGIN_USERNAME_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    session = UserSessionManager(baseActivity)
    binding?.usernameEt?.onTextChanged { onDataChanged() }
    binding?.passEt?.onTextChanged { onDataChanged() }
    setOnClickListener(binding?.forgotTv, binding?.loginBt)
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }
  }

  private fun goBack() {
    requireActivity().finish()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.forgotTv -> {
        WebEngageController.trackEvent(PS_LOGIN_FORGOT_PASSWORD_CLICK, CLICK, NO_EVENT_VALUE)
        addFragmentReplace(com.framework.R.id.container, ForgetPassFragment.newInstance(), true)
      }
      binding?.loginBt -> {
        WebEngageController.trackEvent(PS_LOGIN_FORGOT_PASSWORD_CLICK, CLICK, NO_EVENT_VALUE)
        loginApiVerify(binding?.usernameEt?.text?.toString()?.trim(), binding?.passEt?.text?.toString()?.trim())
      }
    }
  }

  private fun loginApiVerify(userName: String?, password: String?) {
    showProgress()
    viewModel?.verifyUserProfile(UserProfileVerificationRequest(loginKey = userName, loginSecret = password, clientId = clientId))?.observeOnce(viewLifecycleOwner, {
      val response = it as? VerificationRequestResult
      if (response?.isSuccess() == true && response.loginId.isNullOrEmpty().not() && response.validFPIds.isNullOrEmpty().not()) {
        storeUserDetail(response)
      } else {
        hideProgress()
        showShortToast(getString(R.string.ensure_that_the_entered_username_and_password_))
      }
    })
  }

  private fun storeUserDetail(response: VerificationRequestResult) {
    addFragmentReplace(com.framework.R.id.container, FloatingPointAuthFragment.newInstance(response), false)
    hideProgress()
  }

  private fun startDashboard() {
    try {
      hideProgress()
      val dashboardIntent = Intent(requireContext(), Class.forName("com.dashboard.controller.DashboardActivity"))
      dashboardIntent.putExtras(requireActivity().intent)
      val bundle = Bundle()
      bundle.putParcelableArrayList("message", ArrayList())
      dashboardIntent.putExtras(bundle)
      dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      startActivity(dashboardIntent)
      baseActivity.finish()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun startService() {
    baseActivity.startService(Intent(baseActivity, APIService::class.java))
  }

  private fun onDataChanged() {
    val username = binding?.usernameEt?.text?.toString()
    val password = binding?.passEt?.text?.toString()
    binding?.loginBt?.isEnabled = !username.isNullOrBlank() && !password.isNullOrBlank()
  }
}