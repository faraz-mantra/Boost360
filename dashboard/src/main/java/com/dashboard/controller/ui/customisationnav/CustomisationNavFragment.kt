package com.dashboard.controller.ui.customisationnav

import android.os.Bundle
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.customisationnav.model.WebsiteCustomisationItem
import com.dashboard.controller.ui.customisationnav.model.WebsiteCustomisationItem.IconType.*
import com.dashboard.controller.ui.customisationnav.model.WebsiteNavModel
import com.dashboard.databinding.FragmentNavCustomisationBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.DASHBOARD_WEBSITE_PAGE
import com.framework.webengageconstant.PAGE_VIEW
import com.framework.webengageconstant.WEBSITE_CUSTOMISATION_PAGE_LOAD
import java.util.*

class CustomisationNavFragment : AppBaseFragment<FragmentNavCustomisationBinding, DashboardViewModel>(), RecyclerItemClickListener {
  private var session: UserSessionManager? = null

  override fun getLayout(): Int {
    return R.layout.fragment_nav_customisation
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): CustomisationNavFragment {
      val fragment = CustomisationNavFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    this.session = UserSessionManager(baseActivity)
    WebEngageController.trackEvent(WEBSITE_CUSTOMISATION_PAGE_LOAD, PAGE_VIEW, session?.fpTag)
    setRecyclerView()
  }

  private fun setRecyclerView() {
    viewModel?.getWebsiteNavData(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val data = it as? WebsiteNavModel
      binding?.rvNavCustomisation?.adapter = AppBaseRecyclerViewAdapter(baseActivity, data?.websiteCustomisation as ArrayList<WebsiteCustomisationItem>, this)
    })
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.WEBSITE_NAV_CLICK.ordinal -> {
        val aboutAppSectionItem = item as? WebsiteCustomisationItem
        aboutAppSectionItem?.icon?.let { WebsiteCustomisationItem.IconType.fromName(it) }?.let { clickNavButton(it) }
      }
    }
  }

  private fun clickNavButton(type: WebsiteCustomisationItem.IconType) {
    when (type) {
      ic_diamonds -> baseActivity.startBusinessLogo(session)
      ic_fonts -> baseActivity.startWebsiteTheme(session)
      ic_background_images -> baseActivity.startBackgroundImageGallery(session)
      ic_favicon -> baseActivity.startFeviconImage(session)
      ic_featured_image -> baseActivity.startFeatureLogo(session)
    }
  }
}
