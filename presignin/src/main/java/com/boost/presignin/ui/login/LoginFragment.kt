package com.boost.presignin.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentLoginBinding
import com.boost.presignin.helper.ProcessFPDetails
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.fpdetail.UserFpDetailsResponse
import com.boost.presignin.model.login.UserProfileVerificationRequest
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.service.APIService
import com.boost.presignin.ui.mobileVerification.FloatingPointAuthFragment
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import com.framework.extensions.onTextChanged
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientIdThinksity
import com.framework.webengageconstant.*
import java.util.ArrayList
import java.util.HashMap

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
    session = UserSessionManager(baseActivity);
    binding?.usernameEt?.onTextChanged { onDataChanged() }
    binding?.passEt?.onTextChanged { onDataChanged() }
    setOnClickListener(binding?.forgotTv, binding?.loginBt)
    WebEngageController.trackEvent(LOGIN, PAGE_VIEW, NO_EVENT_VALUE)
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener {
      goBack()
    }
    WebEngageController.trackEvent(LOGIN, PAGE_VIEW, NO_EVENT_VALUE)
  }
  private fun goBack() {
    requireActivity().finish()
  }
  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.forgotTv -> {
        addFragmentReplace(com.framework.R.id.container, ForgetPassFragment.newInstance(), true)
      }
      binding?.loginBt -> {
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
    WebEngageController.initiateUserLogin(response.loginId)
    WebEngageController.setUserContactAttributes(response.profileProperties?.userEmail, response.profileProperties?.userMobile, response.profileProperties?.userName, response.sourceClientId)
    WebEngageController.trackEvent(PS_LOGIN_SUCCESS, LOGIN_SUCCESS, NO_EVENT_VALUE)
    session?.userProfileId = response.loginId
    session?.userProfileEmail = response.profileProperties?.userEmail
    session?.userProfileName = response.profileProperties?.userName
    session?.userProfileMobile = response.profileProperties?.userMobile
    session?.setUserLogin(true)
    session?.storeISEnterprise(response.isEnterprise.toString() + "")
    session?.storeIsThinksity((response.sourceClientId != null && response.sourceClientId == clientIdThinksity).toString() + "")
    session?.storeFPID(response.validFPIds?.get(0))
    session?.setAccountSave(true)
    addFragmentReplace(com.framework.R.id.container, FloatingPointAuthFragment.newInstance(fpListAuth = response.authTokens), false)
    hideProgress()
    //loadFpDetails(response)
  }

  private fun loadFpDetails(response: VerificationRequestResult) {
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(response.validFPIds?.get(0) ?: "", map)?.observeOnce(viewLifecycleOwner, {
      val response1 = it as? UserFpDetailsResponse
      if (it.isSuccess() && response1 != null) {
        ProcessFPDetails(session!!).storeFPDetails(response1)
        session?.userProfileId = response1.accountManagerId
        startService()
        startDashboard()
      } else {
        hideProgress()
        showShortToast(getString(R.string.error_getting_fp_detail))
      }
    })
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