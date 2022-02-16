package com.appservice.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel

abstract class AppBaseBottomSheetFragment<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseBottomSheetDialog<Binding, ViewModel>() {


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onCreateView() {
  }

  protected open fun hideProgress() {
    (requireActivity() as? AppBaseActivity<*, *>)?.hideProgress()
  }

  protected open fun showProgress(title: String? = "Please wait...", cancelable: Boolean? = false) {
    (requireActivity() as? AppBaseActivity<*, *>)?.showProgress(title,cancelable)

  }
}
