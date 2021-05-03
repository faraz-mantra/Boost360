package com.dashboard.controller.ui.website_theme

import android.os.Bundle
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.controller.ui.allAddOns.AllBoostAddonsFragment
import com.dashboard.databinding.FragmentWebsiteThemeBinding
import com.framework.models.BaseViewModel

class FragmentWebsiteTheme : AppBaseFragment<FragmentWebsiteThemeBinding, BaseViewModel>() {
  override fun getLayout(): Int {
    return R.layout.fragment_website_theme
  }
  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): FragmentWebsiteTheme {
      val fragment = FragmentWebsiteTheme()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()

  }

}