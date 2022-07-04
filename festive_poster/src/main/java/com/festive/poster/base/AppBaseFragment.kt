package com.festive.poster.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

abstract class AppBaseFragment<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseFragment<Binding, ViewModel>() {

  private var progressView: ProgressDialog? = null
  protected lateinit var sessionLocal: UserSessionManager

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    sessionLocal = UserSessionManager(baseActivity)
    progressView = ProgressDialog.newInstance()
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onCreateView() {
  }

  protected open fun hideProgress() {
    progressView?.hideProgress()
  }

  protected open fun showProgress(title: String? = "Please wait...", cancelable: Boolean? = false) {
    title?.let { progressView?.setTitle(it) }
    cancelable?.let { progressView?.isCancelable = it }
    activity?.let { progressView?.showProgress(it.supportFragmentManager) }
  }
}
