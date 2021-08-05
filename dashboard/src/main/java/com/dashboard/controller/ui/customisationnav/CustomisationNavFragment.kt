package com.dashboard.controller.ui.customisationnav

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentNavCustomisationBinding
import com.dashboard.viewmodel.DashboardViewModel

class CustomisationNavFragment: AppBaseFragment<FragmentNavCustomisationBinding, DashboardViewModel>() {
  override fun getLayout(): Int {
    return R.layout.fragment_nav_customisation
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()

  }
}