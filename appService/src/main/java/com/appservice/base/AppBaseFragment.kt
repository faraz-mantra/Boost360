package com.appservice.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.constant.PreferenceConstant
import com.framework.base.BaseFragment
import com.framework.base.BaseResponse
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

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
}
