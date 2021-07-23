package com.dashboard.controller.ui.more

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.more.model.AboutAppSectionItem
import com.dashboard.controller.ui.more.model.MoreSettingsResponse
import com.dashboard.controller.ui.more.model.UsefulLinksItem
import com.dashboard.databinding.FragmentMoreBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.startAboutBoostActivity
import com.dashboard.utils.startBusinessKycBoost
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import java.util.*

class MoreFragment : AppBaseFragment<FragmentMoreBinding, DashboardViewModel>(), RecyclerItemClickListener {
  private var session: UserSessionManager? = null

  override fun getLayout(): Int {
    return R.layout.fragment_more
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    getData()
    this.session = UserSessionManager(baseActivity)
  }

  private fun getData() {
    viewModel?.getMoreSettings(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val data = it as? MoreSettingsResponse
      binding?.rvUsefulLinks?.adapter = AppBaseRecyclerViewAdapter(baseActivity, data?.usefulLinks as ArrayList<UsefulLinksItem>)
      binding?.rvAbout?.adapter = AppBaseRecyclerViewAdapter(baseActivity, data.aboutAppSection as ArrayList<AboutAppSectionItem>)
    })
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ABOUT_VIEW_CLICK.ordinal -> {
        val aboutAppSectionItem = item as? AboutAppSectionItem
        aboutAppSectionItem?.type?.let { AboutAppSectionItem.IconType.fromName(it) }?.let { clickAppActionButton(it) }
      }
      RecyclerViewActionType.USEFUL_LINKS_CLICK.ordinal -> {
        val usefulLinksItem = item as? UsefulLinksItem
        usefulLinksItem?.type?.let { UsefulLinksItem.IconType.fromName(it) }?.let { clickUsefulButton(it) }

      }
    }
  }

  private fun clickUsefulButton(type: UsefulLinksItem.IconType) {
    when (type) {
      UsefulLinksItem.IconType.business_kyc -> baseActivity.startBusinessKycBoost(session)
    }
  }

  private fun clickAppActionButton(type: AboutAppSectionItem.IconType) {
    when (type) {
      AboutAppSectionItem.IconType.whats_new_version -> baseActivity.startAboutBoostActivity(session!!)

    }

  }
}