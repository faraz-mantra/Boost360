package com.dashboard.controller.ui.academy

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentAcademyBinding
import com.framework.pref.UserSessionManager
import com.dashboard.viewmodel.DashboardViewModel

class AcademyFragment : AppBaseFragment<FragmentAcademyBinding, DashboardViewModel>() {

  private var session: UserSessionManager? = null

  override fun getLayout(): Int {
    return R.layout.fragment_academy
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)

  }

}