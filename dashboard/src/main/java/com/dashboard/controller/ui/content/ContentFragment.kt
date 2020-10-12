package com.dashboard.controller.ui.content

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentContentBinding
import com.framework.models.BaseViewModel

class ContentFragment : AppBaseFragment<FragmentContentBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.fragment_content
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

}