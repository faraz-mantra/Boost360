package com.dashboard.controller.ui.website

import android.os.Bundle
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.controller.ui.dashboard.checkIsPremiumUnlock
import com.dashboard.databinding.FragmentWebsitePagerBinding
import com.dashboard.model.live.websiteItem.WebsiteActionItem
import com.dashboard.model.live.websiteItem.WebsiteData
import com.dashboard.model.live.websiteItem.WebsiteDataResponse
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import java.util.ArrayList

class FragmentCategory : AppBaseFragment<FragmentWebsitePagerBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var adapterWebsite: AppBaseRecyclerViewAdapter<WebsiteActionItem>? = null

  companion object {
    fun newInstance(position: Int): FragmentCategory {
      val bundle = Bundle()
      bundle.putInt(IntentConstant.POSITION.name, position)
      val fragment = FragmentCategory()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_website_pager
  }

  var data: WebsiteData? = null
  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
  }

  override fun onResume() {
    super.onResume()
    getWebsiteData()
  }

  private fun getWebsiteData() {
    viewModel?.getBoostWebsiteItem(baseActivity)?.observeOnce(viewLifecycleOwner, { it0 ->
      val response = it0 as? WebsiteDataResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        data = response.data?.firstOrNull { it.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }
        if (data != null && data?.actionItem.isNullOrEmpty().not()) {
          data?.actionItem?.map { it2 -> if (it2.premiumCode.isNullOrEmpty().not() && session.checkIsPremiumUnlock(it2.premiumCode).not()) it2.isLock = true }
          val position = arguments?.getInt(IntentConstant.POSITION.name)
          setAdapterCustomer(if (position == 0) data?.actionItem!!.filter { it.isFeature == false } else data?.actionItem!!.filter { it.isFeature == true })
        }
      }
    })
  }


  private fun setAdapterCustomer(actionItem: List<WebsiteActionItem>) {
    actionItem as ArrayList<WebsiteActionItem>
    actionItem.map { it.recyclerViewItemType = RecyclerViewItemType.BOOST_WEBSITE_ITEM_VIEW.getLayout() }
    binding?.rvWebsite?.apply {
      adapterWebsite = AppBaseRecyclerViewAdapter(baseActivity, actionItem, this@FragmentCategory)
      adapter = adapterWebsite
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.WEBSITE_ITEM_CLICK.ordinal -> {
        val data = item as? WebsiteActionItem ?: return
        data.type?.let { WebsiteActionItem.IconType.fromName(it) }?.let { clickActionButton(it) }
      }
    }
  }

  private fun clickActionButton(type: WebsiteActionItem.IconType) {
    when (type) {
      WebsiteActionItem.IconType.service_product_catalogue -> baseActivity.startListServiceProduct(session)
      WebsiteActionItem.IconType.latest_update_tips -> session?.let { baseActivity.startUpdateLatestStory(it) }
      WebsiteActionItem.IconType.all_images -> baseActivity.startAllImage(session)
      WebsiteActionItem.IconType.business_profile -> baseActivity.startFragmentsFactory(session, fragmentType = "Business_Profile_Fragment_V2")
      WebsiteActionItem.IconType.testimonials -> baseActivity.startTestimonial(session)
      WebsiteActionItem.IconType.custom_page -> baseActivity.startCustomPage(session)
      WebsiteActionItem.IconType.project_teams -> baseActivity.startListProjectAndTeams(session)
      WebsiteActionItem.IconType.unlimited_digital_brochures -> baseActivity.startListDigitalBrochure(session)
      WebsiteActionItem.IconType.toppers_institute -> baseActivity.startListToppers(session)
      WebsiteActionItem.IconType.upcoming_batches -> baseActivity.startListBatches(session)
      WebsiteActionItem.IconType.faculty_management -> baseActivity.startFacultyMember(session)
      WebsiteActionItem.IconType.places_look_around -> baseActivity.startNearByView(session)
      WebsiteActionItem.IconType.trip_adviser_ratings -> baseActivity.startListTripAdvisor(session)
      WebsiteActionItem.IconType.seasonal_offers -> baseActivity.startListSeasonalOffer(session)
      WebsiteActionItem.IconType.website_theme -> baseActivity.startWebsiteNav(session)// baseActivity.startWebsiteTheme(session)
    }
  }
}