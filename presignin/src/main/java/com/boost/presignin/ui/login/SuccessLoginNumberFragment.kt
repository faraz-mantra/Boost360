package com.boost.presignin.ui.login

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.boost.presignin.R
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentSuccessLoginBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.ui.mobileVerification.AuthBaseFragment
import com.boost.presignin.ui.mobileVerification.FP_LIST_FRAGMENT
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.FRAGMENT_TYPE
import com.framework.utils.hideKeyBoard
import com.framework.webengageconstant.*

class SuccessLoginNumberFragment : AuthBaseFragment<FragmentSuccessLoginBinding>() {

  private val resultLogin by lazy { arguments?.getSerializable(IntentConstant.EXTRA_FP_LIST_AUTH.name) as? VerificationRequestResult }

  companion object {

    @JvmStatic
    fun newInstance(bundle: Bundle?) = SuccessLoginNumberFragment().apply {
      arguments = bundle
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_success_login
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun resultLogin(): VerificationRequestResult? {
    return resultLogin
  }

  override fun authTokenData(): AuthTokenDataItem? {
    return if (resultLogin()?.authTokens.isNullOrEmpty().not()) resultLogin()?.authTokens!![0] else null
  }

  override fun onCreateView() {
    super.onCreateView()
    baseActivity.hideKeyBoard()
    WebEngageController.trackEvent(PS_USER_LOGIN_SUCCESS_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.numberTv?.text = "+91 ${resultLogin?.profileProperties?.userMobile}"
    setOnClickListener(binding?.goDashboardBt, binding?.changeNumberBtn)
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }
  }

  private fun goBack() {
    baseActivity.finish()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.goDashboardBt -> {
        WebEngageController.trackEvent(PS_USER_LOGIN_SUCCESS_DASHBOARD_CLICK, CLICK, NO_EVENT_VALUE)
        if (resultLogin?.authTokens?.size == 1) {
          authTokenData()?.createAccessTokenAuth()
        } else {
          navigator?.startActivity(MobileVerificationActivity::class.java, Bundle().apply {
            putInt(FRAGMENT_TYPE, FP_LIST_FRAGMENT);putSerializable(IntentConstant.EXTRA_FP_LIST_AUTH.name, resultLogin)
          })
        }
      }
      binding?.changeNumberBtn -> {
        WebEngageController.trackEvent(PS_USER_LOGIN_SUCCESS_CHANGE_NUMBER_CLICK, CLICK, NO_EVENT_VALUE)
        navigator?.startActivity(MobileVerificationActivity::class.java)
      }
    }
  }
}