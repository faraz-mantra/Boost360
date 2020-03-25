package com.onboarding.nowfloats.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel


abstract class AppBaseFragment<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseFragment<Binding, ViewModel>() {


  protected var appBaseActivity: AppBaseActivity<*, *>? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    appBaseActivity = activity as? AppBaseActivity<*, *>
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onCreateView() {

  }

}
