package com.dashboard.controller.ui.help

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentContentBinding
import com.framework.models.BaseViewModel

class HelpFragment : AppBaseFragment<FragmentContentBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.fragment_help
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

}