package com.dashboard.controller.ui.website

import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.controller.getDomainName
import com.dashboard.controller.ui.dashboard.checkIsPremiumUnlock
import com.dashboard.databinding.FragmentWebsiteBinding
import com.dashboard.model.live.websiteItem.WebsiteActionItem
import com.dashboard.model.live.websiteItem.WebsiteDataResponse
import com.framework.pref.BASE_IMAGE_URL
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.framework.webengageconstant.PAGE_VIEW
import com.framework.webengageconstant.DASHBOARD_WEBSITE_PAGE
import java.util.*

class WebsiteFragment : AppBaseFragment<FragmentWebsiteBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var adapterWebsite: AppBaseRecyclerViewAdapter<WebsiteActionItem>? = null

  override fun getLayout(): Int {
    return R.layout.fragment_website
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    getWebsiteData()
    setOnClickListener(binding?.txtDomainName, binding?.btnProfileLogo, binding?.editProfile, binding?.businessAddress, binding?.contactDetail, binding?.businessTiming)

    WebEngageController.trackEvent(DASHBOARD_WEBSITE_PAGE, PAGE_VIEW, session?.fpTag)
  }

  override fun onResume() {
    super.onResume()
    setUserData()
  }

  private fun setUserData() {
    val desc = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)
    binding?.txtDesc?.text = if (desc.isNullOrEmpty().not()) desc else ""
    binding?.txtBusinessName?.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
    binding?.txtDomainName?.text = fromHtml("<u>${session!!.getDomainName()}</u>")
    var imageUri = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("BizImages") && imageUri!!.contains("http").not()) {
      imageUri = BASE_IMAGE_URL + imageUri
    }
    binding?.imgProfileLogo?.apply {
      if (imageUri.isNullOrEmpty().not()) {
        baseActivity.glideLoad(mImageView = this, url = imageUri!!, placeholder = R.drawable.gradient_white, isLoadBitmap = true)
      } else setImageResource(R.drawable.ic_add_logo_d)
    }
    updateTimings()
  }

  private fun getWebsiteData() {
    viewModel?.getBoostWebsiteItem(baseActivity)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer { it0 ->
      val response = it0 as? WebsiteDataResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val data = response.data?.firstOrNull { it.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }
        if (data != null && data.actionItem.isNullOrEmpty().not()) {
          data.actionItem!!.map { it2 -> if (it2.premiumCode.isNullOrEmpty().not() && session.checkIsPremiumUnlock(it2.premiumCode).not()) it2.isLock = true }
          binding?.mainContent?.setBackgroundColor(getColor(baseActivity, if (data.actionItem!!.size % 2 != 0) R.color.white_smoke_1 else R.color.white))
          setAdapterCustomer(data.actionItem!!)
        }
      }
    })
  }

  private fun setAdapterCustomer(actionItem: ArrayList<WebsiteActionItem>) {
    actionItem.map { it.recyclerViewItemType = RecyclerViewItemType.BOOST_WEBSITE_ITEM_VIEW.getLayout() }
    if (adapterWebsite == null) {
      binding?.rvEnquiries?.apply {
        adapterWebsite = AppBaseRecyclerViewAdapter(baseActivity, actionItem, this@WebsiteFragment)
        adapter = adapterWebsite
      }
    } else adapterWebsite?.notify(actionItem)
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
      WebsiteActionItem.IconType.website_theme -> baseActivity.startWebsiteTheme(session)
    }
  }


  private fun updateTimings() {
    var isOpen = false
    when (Calendar.getInstance()[Calendar.DAY_OF_WEEK]) {
      Calendar.SUNDAY -> {
        isOpen = (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("am")
            || session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("pm"))
      }
      Calendar.MONDAY -> {
        isOpen = (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("am")
            || session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("pm"))
      }
      Calendar.TUESDAY -> {
        isOpen = (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("am")
            || session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("pm"))
      }
      Calendar.WEDNESDAY -> {
        isOpen = (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("am")
            || session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("pm"))
      }
      Calendar.THURSDAY -> {
        isOpen = (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("am")
            || session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("pm"))
      }
      Calendar.FRIDAY -> {
        isOpen = (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("am")
            || session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("pm"))
      }
      Calendar.SATURDAY -> {
        isOpen = (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("am")
            || session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME)!!.toLowerCase(Locale.ROOT).endsWith("pm"))
      }
    }
    binding?.txtOpenClose?.text = resources.getString(if (isOpen) R.string.open_now else R.string.close_now)
    binding?.ellipseOpenClose?.setTintColor(getColor(baseActivity, if (isOpen) R.color.green_light else R.color.red_F40000))

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.txtDomainName -> baseActivity.startWebViewPageLoad(session, session!!.getDomainName(false))
      binding?.btnProfileLogo -> baseActivity.startFeatureLogo(session)
      binding?.editProfile -> baseActivity.startBusinessProfileDetailEdit(session)
      binding?.businessAddress -> baseActivity.startBusinessAddress(session)
      binding?.contactDetail -> baseActivity.startBusinessInfoEmail(session)
      binding?.businessTiming -> baseActivity.startBusinessHours(session)
    }
  }
}