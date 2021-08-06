package com.dashboard.controller.ui.customisationnav

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import com.appservice.ui.updatesBusiness.showDialog
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.FragmentType
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.startFragmentDashboardActivity
import com.dashboard.controller.ui.business.BusinessProfileFragment
import com.dashboard.controller.ui.customisationnav.model.WebsiteCustomisationItem
import com.dashboard.controller.ui.customisationnav.model.WebsiteCustomisationItem.IconType.*
import com.dashboard.controller.ui.customisationnav.model.WebsiteNavModel
import com.dashboard.controller.ui.more.model.AboutAppSectionItem
import com.dashboard.controller.ui.more.model.UsefulLinksItem
import com.dashboard.databinding.FragmentNavCustomisationBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.*
import java.util.*

class CustomisationNavFragment : AppBaseFragment<FragmentNavCustomisationBinding, DashboardViewModel>(), RecyclerItemClickListener {
  private var session: UserSessionManager?=null

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
      ic_diamonds -> baseActivity.startFeviconImage(session)
      ic_fonts ->   baseActivity.startWebsiteTheme(session)
      ic_background_images ->   baseActivity.startBackgroundImageGallery(session)
      ic_favicon ->   baseActivity.startFeviconImage(session)
      ic_featured_image ->   baseActivity.startFeatureLogo(session)
    }

  }

}
