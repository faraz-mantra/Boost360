package com.dashboard.controller.ui.website

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentWebsiteBinding
import com.framework.models.BaseViewModel

class WebsiteFragment : AppBaseFragment<FragmentWebsiteBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.fragment_website
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

}