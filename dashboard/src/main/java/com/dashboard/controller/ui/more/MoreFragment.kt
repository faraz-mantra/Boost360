package com.dashboard.controller.ui.more

import android.graphics.Color
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.controller.ui.more.model.AboutAppSectionItem
import com.dashboard.controller.ui.more.model.MoreSettingsResponse
import com.dashboard.controller.ui.more.model.UsefulLinksItem
import com.dashboard.databinding.FragmentMoreBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import java.util.ArrayList

class MoreFragment : AppBaseFragment<FragmentMoreBinding, DashboardViewModel>() {
  override fun getLayout(): Int {
    return R.layout.fragment_more
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
   getData()
  }

  private fun getData() {
    viewModel?.getMoreSettings(baseActivity)?.observeOnce(viewLifecycleOwner,{
      val data = it as? MoreSettingsResponse
      binding?.rvUsefulLinks?.adapter = AppBaseRecyclerViewAdapter(baseActivity,data?.usefulLinks as ArrayList<UsefulLinksItem>)
      binding?.rvAbout?.adapter = AppBaseRecyclerViewAdapter(baseActivity, data.aboutAppSection as ArrayList<AboutAppSectionItem>)
    })
  }
}