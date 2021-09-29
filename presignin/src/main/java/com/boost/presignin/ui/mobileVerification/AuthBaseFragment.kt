package com.boost.presignin.ui.mobileVerification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.helper.ProcessFPDetails
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.accessToken.AccessTokenRequest
import com.boost.presignin.model.authToken.AccessTokenResponse
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.fpdetail.UserFpDetailsResponse
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.service.APIService
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.analytics.SentryController
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientIdThinksity
import com.framework.pref.saveAccessTokenAuth
import com.framework.webengageconstant.*
import java.util.HashMap

abstract class AuthBaseFragment<Binding : ViewDataBinding> : AppBaseFragment<Binding, LoginSignUpViewModel>() {

  protected lateinit var session: UserSessionManager

  protected abstract fun resultLogin(): VerificationRequestResult?
  protected abstract fun authTokenData(): AuthTokenDataItem?

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }


  override fun onCreateView() {
    super.onCreateView()
    this.session = UserSessionManager(baseActivity)
  }

  protected fun AuthTokenDataItem.createAccessTokenAuth() {
    showProgress()
    WebEngageController.initiateUserLogin(resultLogin()?.loginId)
    WebEngageController.setUserContactAttributes(
      resultLogin()?.profileProperties?.userEmail,
      resultLogin()?.profileProperties?.userMobile,
      resultLogin()?.profileProperties?.userName,
      resultLogin()?.sourceClientId
    )
    WebEngageController.setFPTag(this.floatingPointTag)
    WebEngageController.trackEvent(PS_LOGIN_SUCCESS, LOGIN_SUCCESS, NO_EVENT_VALUE)
    session.userProfileId = resultLogin()?.loginId
    session.userProfileEmail = resultLogin()?.profileProperties?.userEmail
    session.userProfileName = resultLogin()?.profileProperties?.userName
    session.userProfileMobile = resultLogin()?.profileProperties?.userMobile
    session.storeISEnterprise(resultLogin()?.isEnterprise.toString() + "")
    session.storeIsThinksity((resultLogin()?.sourceClientId != null && resultLogin()?.sourceClientId == clientIdThinksity).toString() + "")
    session.storeFPID(this.floatingPointId)
    session.storeFpTag(this.floatingPointTag)
    session.setUserLogin(true)
    val request = AccessTokenRequest(
      authToken = this.authenticationToken,
      clientId = clientId,
      fpId = this.floatingPointId
    )
    viewModel?.createAccessToken(request)?.observeOnce(viewLifecycleOwner, {
      val result = it as? AccessTokenResponse
      if (it?.isSuccess() == true && result?.result != null) {
        session.saveAccessTokenAuth(result.result!!)
        this.storeFpDetails()
      } else {
        hideProgress()
        showLongToast(getString(R.string.access_token_create_error))
      }
    })
  }

  private fun AuthTokenDataItem.storeFpDetails() {
    WebEngageController.trackEvent(
      PS_BUSINESS_ACCOUNT_CHOOSE,
      CHOOSE_BUSINESS,
      this.floatingPointId ?: ""
    )
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(this.floatingPointId ?: "", map)?.observeOnce(viewLifecycleOwner, {
      val response = it as? UserFpDetailsResponse
      if (it.isSuccess() && response != null) {
        ProcessFPDetails(session).storeFPDetails(response)
        SentryController.setUser(UserSessionManager(baseActivity))
        startService()
        startDashboard()
      } else {
        hideProgress()
        showShortToast(getString(R.string.error_getting_fp_detail))
      }
    })
  }

  private fun startService() {
    baseActivity.startService(Intent(baseActivity, APIService::class.java))
  }

  protected fun startDashboard() {
    try {
      val dashboardIntent =
        Intent(baseActivity, Class.forName("com.dashboard.controller.DashboardActivity"))
      dashboardIntent.putExtras(requireActivity().intent)
      val bundle = Bundle()
      bundle.putParcelableArrayList("message", ArrayList())
      dashboardIntent.putExtras(bundle)
      dashboardIntent.flags =
        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      startActivity(dashboardIntent)
      baseActivity.finish()
      hideProgress()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}