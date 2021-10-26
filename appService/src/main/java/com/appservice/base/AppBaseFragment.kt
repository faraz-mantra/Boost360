package com.appservice.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.constant.PreferenceConstant
import com.appservice.model.StatusKyc
import com.framework.analytics.SentryController
import com.framework.base.BaseFragment
import com.framework.base.BaseResponse
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.models.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.onboarding.nowfloats.ui.webview.WebViewTNCDialog

abstract class AppBaseFragment<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseFragment<Binding, ViewModel>() {

  private var progressView: ProgressDialog? = null
  protected lateinit var sessionLocal: UserSessionManager

  protected val pref: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(
        PreferenceConstant.NOW_FLOATS_PREFS,
        Context.MODE_PRIVATE
      )
    }

  override fun onCreateView() {
    progressView = ProgressDialog.newInstance()
    sessionLocal = UserSessionManager(baseActivity)
  }

  protected open fun hideProgress() {
    progressView?.hideProgress()
  }

  protected open fun showProgress(title: String? = "Please wait...", cancelable: Boolean? = false) {
    title?.let { progressView?.setTitle(it) }
    cancelable?.let { progressView?.isCancelable = it }
    activity?.let { progressView?.showProgress(it.supportFragmentManager) }
  }

  protected fun isResponseSuccessful(it: BaseResponse, errorMessage: String?): Boolean {
    if ((it.error is NoNetworkException).not()) {
      if ((it.isSuccess())) {
        return true
      } else {
        Log.d("API_ERROR", it.message())
        Log.d("BaseResponseData", it.message())
        showErrorMessage(errorMessage)
      }
    } else {
      showErrorMessage(resources.getString(R.string.internet_connection_not_available))
    }
    return false
  }

  protected fun hitApi(liveData: LiveData<BaseResponse>?, errorStringId: Int) {
    liveData?.observeOnce(viewLifecycleOwner, Observer {
      if (isResponseSuccessful(it, resources.getString(errorStringId))) {
        onSuccess(it)
      } else {
        onFailure(it)
      }
    })
  }

  open fun onSuccess(it: BaseResponse) {
    Log.d("TAG", "onSuccess")
  }

  open fun onFailure(it: BaseResponse) {

  }

  private fun showErrorMessage(string: String?) {
    hideProgress()
    showLongToast(string)
  }

  fun showAlertCapLimit(msg: String,buyItemKey: String = "") {
    val builder = AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme))
    builder.setCancelable(false)
    builder.setTitle("You have exceeded limit!").setMessage(msg).setPositiveButton("Explore Add-ons") { dialog, which ->
      dialog.dismiss()
      startStorePage(CapLimitFeatureResponseItem.FeatureType.UNLIMITED_CONTENT.name)
      baseActivity.finish()
    }.setNegativeButton("Close") { dialog, _ ->
      dialog.dismiss()
      baseActivity.finish()
    }
    builder.create().show()
  }

  fun startStorePage(buyItemKey: String = "") {
    try {
      showProgress("Loading. Please wait...")
      val intent = Intent(baseActivity, Class.forName("com.boost.upgrades.UpgradeActivity"))
      intent.putExtra("expCode", sessionLocal.fP_AppExperienceCode)
      intent.putExtra("fpName", sessionLocal.fpTag)
      intent.putExtra("fpid", sessionLocal.fPID)
      intent.putExtra("fpTag", sessionLocal.fpTag)
      intent.putExtra("accountType", sessionLocal.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
      intent.putStringArrayListExtra("userPurchsedWidgets", ArrayList(sessionLocal.getStoreWidgets() ?: ArrayList()))
      intent.putExtra("email", sessionLocal.userProfileEmail ?: "ria@nowfloats.com")
      intent.putExtra("mobileNo", sessionLocal.userPrimaryMobile ?: "9160004303")
      intent.putExtra("profileUrl", sessionLocal.fPLogo)
      intent.putExtra("buyItemKey", buyItemKey)
      baseActivity.startActivity(intent)
      Handler().postDelayed({ hideProgress() }, 1000)
    } catch (e: Exception) {
      showLongToast("Unable to start upgrade activity.")
      SentryController.captureException(e)
    }
  }


  fun getStaffType(category_code:String?):String{
    return when(category_code){
      "DOC", "HOS"->"DOCTORS"
      else ->"STAFF"
    }
  }

  fun isDoctorProfile(category_code:String?): Boolean {
    return when(category_code){
      "DOC", "HOS"-> true
      else ->false
    }
  }
}

