package com.dashboard.controller.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.appservice.model.onboardingUpdate.OnBoardingUpdateModel
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.FragmentType
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.DashboardActivity
import com.dashboard.controller.getDomainName
import com.dashboard.controller.startFragmentDashboardActivity
import com.dashboard.databinding.FragmentDashboardBinding
import com.dashboard.model.*
import com.dashboard.model.live.addOns.ManageBusinessData
import com.dashboard.model.live.addOns.ManageBusinessDataResponse
import com.dashboard.model.live.dashboardBanner.*
import com.dashboard.model.live.premiumBanner.*
import com.dashboard.model.live.quickAction.QuickActionItem
import com.dashboard.model.live.quickAction.QuickActionResponse
import com.dashboard.model.live.shareUser.ShareUserDetailResponse
import com.dashboard.model.live.siteMeter.SiteMeterScoreDetails
import com.dashboard.pref.*
import com.dashboard.pref.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME
import com.dashboard.pref.Key_Preferences.GET_FP_DETAILS_LogoUrl
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.*
import com.framework.utils.DateUtils.FORMAT_DD_MM_YYYY_N
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.getDateMillSecond
import com.framework.utils.DateUtils.parseDate
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.inventoryorder.model.floatMessage.MessageModel
import com.inventoryorder.model.mapDetail.VisitsModelResponse
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.summary.SummaryEntity
import com.inventoryorder.model.summary.UserSummaryResponse
import com.inventoryorder.model.summaryCall.CallSummaryResponse
import com.inventoryorder.rest.response.OrderSummaryResponse
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.LocalSessionModel
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.MyDigitalCardShareDialog
import com.onboarding.nowfloats.ui.webview.WebViewBottomDialog
import java.util.*
import kotlin.collections.ArrayList

const val IS_FIRST_LOAD = "isFirsLoad"

class DashboardFragment : AppBaseFragment<FragmentDashboardBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var adapterBusinessContent: AppBaseRecyclerViewAdapter<BusinessContentSetupData>? = null
  private var channelAdapter: AppBaseRecyclerViewAdapter<ChannelData>? = null
  private var adapterPagerBusinessUpdate: AppBaseRecyclerViewAdapter<BusinessSetupHighData>? = null
  private var adapterRoi: AppBaseRecyclerViewAdapter<RoiSummaryData>? = null
  private var adapterGrowth: AppBaseRecyclerViewAdapter<GrowthStatsData>? = null
  private var adapterMarketBanner: AppBaseRecyclerViewAdapter<DashboardMarketplaceBanner>? = null
  private var adapterAcademy: AppBaseRecyclerViewAdapter<DashboardAcademyBanner>? = null
  private var siteMeterData: SiteMeterScoreDetails? = null

  override fun getLayout(): Int {
    return R.layout.fragment_dashboard
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    setOnClickListener(binding?.btnBusinessLogo, binding?.btnNotofication, binding?.btnVisitingCard,binding?.txtDomainName)
    val versionName: String = baseActivity.packageManager.getPackageInfo(baseActivity.packageName, 0).versionName
    binding?.txtVersion?.text = "Version $versionName"
    apiSellerSummary()
    getPremiumBanner()
    WebEngageController.trackEvent("Dashboard Home Page", "pageview", session?.fpTag)
  }

  private fun getPremiumBanner() {
    setDataMarketBanner(getMarketPlaceBanners() ?: ArrayList())
    setDataRiaAcademy(getAcademyBanners() ?: ArrayList())
    viewModel?.getUpgradeDashboardBanner()?.observeOnce(viewLifecycleOwner, {
      val response = it as? DashboardPremiumBannerResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val data = response.data?.get(0)
        if (data?.academyBanners.isNullOrEmpty().not()) {
          saveDataAcademy(data?.academyBanners!!)
          setDataRiaAcademy(data.academyBanners!!)
        }
        if (data?.marketplaceBanners.isNullOrEmpty().not()) {
          val marketBannerFilter = (data?.marketplaceBanners ?: ArrayList()).marketBannerFilter(session)
          saveDataMarketPlace(marketBannerFilter)
          setDataMarketBanner(marketBannerFilter)
        }
      }
    })
  }

  override fun onResume() {
    super.onResume()
    baseActivity.runOnUiThread { getFloatMessage() }
  }

  private fun getFloatMessage() {
    if (isFirstLoad().not()) binding?.progress?.visible()
    session?.siteMeterData { it?.let { it1 -> refreshData(it1) } }
    viewModel?.getBizFloatMessage(session!!.getRequestFloat())?.observeOnce(this, {
      if (it?.isSuccess() == true) (it as? MessageModel)?.saveData()
      session?.siteMeterData { it1 -> it1?.let { it2 -> refreshData(it2) } }
      if (isFirstLoad().not()) binding?.progress?.gone()
      saveFirstLoad()
    })
  }

  private fun refreshData(siteMeterData: SiteMeterScoreDetails) {
    this.siteMeterData = siteMeterData
    (baseActivity as? DashboardActivity)?.setPercentageData(siteMeterData.siteMeterTotalWeight)
    setUserData()
    setRecBusinessManageTask()
    getNotificationCount()
    getSiteMeter(siteMeterData)
    setDataSellerSummary(OrderSummaryModel().getSellerSummary(), getSummaryDetail(), CallSummaryResponse().getCallSummary())
  }

  private fun getSiteMeter(siteMeterData: SiteMeterScoreDetails) {
    if (session?.siteHealth != siteMeterData.siteMeterTotalWeight) {
      session?.siteHealth = siteMeterData.siteMeterTotalWeight
      val data = OnBoardingUpdateModel()
      data.setData(session?.fPID!!, String.format("site_health:%s", siteMeterData.siteMeterTotalWeight))
      viewModel?.fpOnboardingUpdate(WA_KEY, data)?.observeOnce(viewLifecycleOwner, {
        Log.i("DASHBOARD", it.message())
      })
    }
  }

  private fun setRecBusinessManageTask() {
    binding?.lowRecommendedTask?.apply {
      viewModel?.getQuickActionData(baseActivity)?.observeOnce(viewLifecycleOwner, {
        val response = it as? QuickActionResponse
        val listAction = response?.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }
        if (response?.isSuccess() == true && listAction?.actionItem.isNullOrEmpty().not()) {
          rvQuickAction.apply {
            val adapterQuickAction = AppBaseRecyclerViewAdapter(baseActivity, listAction?.actionItem!!, this@DashboardFragment)
            adapter = adapterQuickAction
          }
        } else showShortToast(baseActivity.getString(R.string.quick_action_data_error))
      })

    }
    binding?.lowManageBusiness?.apply {
      title.text = if (getRoiSummaryType(session?.fP_AppExperienceCode) == "DOC") baseActivity.getString(R.string.manage_your_clinic) else baseActivity.getString(R.string.manage_your_business)
      rvManageBusiness.apply {
        viewModel?.getBoostAddOnsTop(baseActivity)?.observeOnce(viewLifecycleOwner, {
          val response = it as? ManageBusinessDataResponse
          val dataAction = response?.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }
          if (dataAction != null && dataAction.actionItem.isNullOrEmpty().not()) {
            dataAction.actionItem?.map { it1 -> if (it1.premiumCode.isNullOrEmpty().not() && session.checkIsPremiumUnlock(it1.premiumCode).not()) it1.isLock = true }
            val adapterBusinessData = AppBaseRecyclerViewAdapter(baseActivity, dataAction.actionItem!!, this@DashboardFragment)
            adapter = adapterBusinessData
          } else showShortToast(baseActivity.getString(R.string.manage_business_not_found))
        })
      }
      btnShowAll.setOnClickListener {
        WebEngageController.trackEvent("Business Add-ons Page", "Add-ons", session?.fpTag)
        startFragmentDashboardActivity(FragmentType.ALL_BOOST_ADD_ONS)
      }
    }
  }

  private fun apiSellerSummary() {
    viewModel?.getSellerSummary(clientId_ORDER, session?.fpTag)?.observeOnce(viewLifecycleOwner, {
      val response1 = it as? OrderSummaryResponse
      if (response1?.isSuccess() == true && response1.Data != null) response1.Data?.saveData()
      val scope = if (session?.iSEnterprise == "true") "1" else "0"
      viewModel?.getUserSummary(clientId, session?.fPParentId, scope)?.observeOnce(viewLifecycleOwner, { it1 ->
        val response2 = it1 as? UserSummaryResponse
        response2?.getSummary()?.noOfSubscribers = session?.subcribersCount?.toIntOrNull() ?: 0
        session?.saveUserSummary(response2?.getSummary())
        val identifierType = if (session?.iSEnterprise == "true") "MULTI" else "SINGLE"
        viewModel?.getUserCallSummary(clientId, session?.fPParentId, identifierType)?.observeOnce(viewLifecycleOwner, { it2 ->
          val response3 = it2 as? CallSummaryResponse
          response3?.saveData()
          viewModel?.getMapVisits(session?.fpTag, session?.getRequestMap())?.observeOnce(viewLifecycleOwner, { it3 ->
            val response4 = it3 as? VisitsModelResponse
            session?.mapVisitsCount = response4?.getTotalCount() ?: "0"
            setDataSellerSummary(response1?.Data, response2?.getSummary(), response3)
          })
        })
      })
    })
  }

  private fun getNotificationCount() {
    viewModel?.getNotificationCount(clientId, session?.fPID)?.observeOnce(viewLifecycleOwner, {
      val value = (it.anyResponse as? Double)?.toInt()
      if (it.isSuccess() && (value != null && value != 0)) {
        binding?.txtNotification?.visibility = View.VISIBLE
        binding?.txtNotification?.text = "$value+"
      } else binding?.txtNotification?.visibility = View.INVISIBLE
    })
  }

  private fun setDataSellerSummary(sellerOrder: OrderSummaryModel?, summary: SummaryEntity?, callSummary: CallSummaryResponse?) {
    val data = BusinessSetupHighData().getData(siteMeterData?.siteMeterTotalWeight ?: 0,
        summary?.getNoOfUniqueViews() ?: "0", sellerOrder?.getTotalOrders() ?: "0", getCustomerTypeFromServiceCode(session?.fP_AppExperienceCode), summary?.getNoOfMessages() ?: "0")
    if (adapterPagerBusinessUpdate == null) {
      binding?.pagerBusinessSetupHigh?.apply {
        adapterPagerBusinessUpdate = AppBaseRecyclerViewAdapter(baseActivity, data, this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterPagerBusinessUpdate
        binding?.dotIndicatorBusinessHigh?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }
    } else adapterPagerBusinessUpdate?.notify(data)
    val roiData = RoiSummaryData().getData(summary?.getNoOfMessages() ?: "0", callSummary?.getTotalCalls() ?: "0", sellerOrder, getRoiSummaryType(session?.fP_AppExperienceCode))
    if (adapterRoi == null) {
      binding?.rvRoiSummary?.apply {
        adapterRoi = AppBaseRecyclerViewAdapter(baseActivity, roiData, this@DashboardFragment)
        adapter = adapterRoi
      }
    } else adapterRoi?.notify(roiData)
    val growthStatsList = GrowthStatsData().getData(summary, session)
    if (adapterGrowth == null) {
      binding?.rvGrowthState?.apply {
        adapterGrowth = AppBaseRecyclerViewAdapter(baseActivity, growthStatsList, this@DashboardFragment)
        adapter = adapterGrowth
      }
    } else adapterGrowth?.notify(growthStatsList)
  }

  private fun setUserData() {
    binding?.txtBusinessName?.text = session?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME)
    binding?.txtDomainName?.text = fromHtml("<u>${session!!.getDomainName(true)}</u>")
    var imageLogoUri = session?.getFPDetails(GET_FP_DETAILS_LogoUrl)
    if (imageLogoUri.isNullOrEmpty().not() && imageLogoUri!!.contains("http").not()) {
      imageLogoUri = BASE_IMAGE_URL + imageLogoUri
    }
    binding?.imgBusinessLogo?.let { baseActivity.glideLoad(it, imageLogoUri, R.drawable.ic_add_logo_d, isCrop = true) }
  }

  private fun setDataRiaAcademy(academyBanner: ArrayList<DashboardAcademyBanner>) {
    binding?.pagerRiaAcademy?.apply {
      if (academyBanner.isNotEmpty()) {
        binding?.riaAcademyView?.visible()
        if (adapterAcademy == null) {
          adapterAcademy = AppBaseRecyclerViewAdapter(baseActivity, academyBanner, this@DashboardFragment)
          offscreenPageLimit = 3
          adapter = adapterAcademy
          setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        } else adapterAcademy?.notify(academyBanner)
      } else binding?.riaAcademyView?.gone()
    }
  }

  private fun setDataMarketBanner(marketBannerFilter: ArrayList<DashboardMarketplaceBanner>) {
    binding?.pagerBoostPremium?.apply {
      if (marketBannerFilter.isNotEmpty()) {
        binding?.boostPremiumView?.visible()
        if (adapterMarketBanner == null) {
          adapterMarketBanner = AppBaseRecyclerViewAdapter(baseActivity, marketBannerFilter, this@DashboardFragment)
          offscreenPageLimit = 3
          adapter = adapterMarketBanner
          setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        } else adapterMarketBanner?.notify(marketBannerFilter)
      } else binding?.boostPremiumView?.gone()
    }
  }

  private fun showErrorChannel(message: String) {
    showShortToast(message)
    hideProgress()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.READING_SCORE_CLICK.ordinal -> {
        WebEngageController.trackEvent("SITE HEALTH Page", "SITE_HEALTH", session?.fpTag);
        session?.let { baseActivity.startOldSiteMeter(it) }
//        startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, 0) })
      }
      RecyclerViewActionType.BUSINESS_SETUP_SCORE_CLICK.ordinal -> startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, position) })
      RecyclerViewActionType.QUICK_ACTION_ITEM_CLICK.ordinal -> {
        val data = item as? QuickActionItem ?: return
        QuickActionItem.QuickActionType.from(data.quickActionType)?.let { quickActionClick(it) }
      }
      RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal -> {
        val data = item as? ManageBusinessData ?: return
        ManageBusinessData.BusinessType.fromName(data.businessType)?.let { com.dashboard.controller.ui.allAddOns.businessAddOnsClick(it, baseActivity, session) }
      }
      RecyclerViewActionType.BUSINESS_UPDATE_CLICK.ordinal -> {
        val data = item as? Specification ?: return
        BusinessSetupHighData.BusinessClickEvent.fromName(data.clickType)?.let { clickBusinessUpdate(it) }
      }
      RecyclerViewActionType.ROI_SUMMARY_CLICK.ordinal -> {
        val data = item as? RoiSummaryData ?: return
        RoiSummaryData.RoiType.fromName(data.type)?.let { clickRoiSummary(it) }
      }
      RecyclerViewActionType.GROWTH_STATS_CLICK.ordinal -> {
        val data = item as? GrowthStatsData ?: return
        GrowthStatsData.GrowthType.fromName(data.type)?.let { clickGrowthStats(it) }
      }
      RecyclerViewActionType.CHANNEL_ITEM_CLICK.ordinal -> {
        val data = (item as? ChannelData)?.channelData ?: return
        actionChannelClick(data)
      }
      RecyclerViewActionType.PROMO_BANNER_CLICK.ordinal -> {
        val data = item as? DashboardMarketplaceBanner ?: return
        if (data.ctaFeatureKey.isNullOrEmpty().not()) {
          session?.let { baseActivity.initiateAddonMarketplace(it, false, "", data.ctaFeatureKey) }
        }
      }
      RecyclerViewActionType.PROMO_BOOST_ACADEMY_CLICK.ordinal -> {
        val data = item as? DashboardAcademyBanner ?: return
        academyBannerBoostClick(data)
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnNotofication -> session?.let { baseActivity.startNotification(it) }
      binding?.btnBusinessLogo -> baseActivity.startBusinessLogo(session)
      binding?.btnVisitingCard -> visitingCard()
      binding?.txtDomainName -> baseActivity.startWebViewPageLoad(session, session!!.getDomainName(false))
    }
  }

  private fun visitingCard() {
    session?.let {
      val dialogCard = MyDigitalCardShareDialog()
      dialogCard.setData(getLocalSession(it))
      dialogCard.show(this@DashboardFragment.parentFragmentManager, MyDigitalCardShareDialog::class.java.name)
    }
  }

  private fun bottomSheetWebView(title: String, domainUrl: String) {
    val webViewBottomDialog = WebViewBottomDialog()
    webViewBottomDialog.setData(title, domainUrl)
    webViewBottomDialog.show(this@DashboardFragment.parentFragmentManager, WebViewBottomDialog::class.java.name)
  }

  private fun shareUserDetail(isWhatsApp: Boolean) {
    viewModel?.getBoostUserDetailMessage(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val response = it as? ShareUserDetailResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val messageDetail = response.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }?.message
        if (messageDetail.isNullOrEmpty().not()) {
          val txt = String.format(messageDetail!!, session?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME) ?: "", session!!.getDomainName(false), session?.userPrimaryMobile, session?.fPEmail)
          try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            if (isWhatsApp) intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, txt)
            baseActivity.startActivity(Intent.createChooser(intent, "Share your business detail..."))
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }
    })
  }

  private fun quickActionClick(type: QuickActionItem.QuickActionType) {
    when (type) {
      QuickActionItem.QuickActionType.POST_NEW_UPDATE -> baseActivity.startPostUpdate(session)
      QuickActionItem.QuickActionType.ADD_PHOTO_GALLERY -> baseActivity.startAddImageGallery(session)
      QuickActionItem.QuickActionType.ADD_TESTIMONIAL -> baseActivity.startAddTestimonial(session, true)
      QuickActionItem.QuickActionType.ADD_CUSTOM_PAGE -> baseActivity.startCreateCustomPage(session, true)
      QuickActionItem.QuickActionType.LIST_SERVICES,
      QuickActionItem.QuickActionType.LIST_PRODUCT,
      QuickActionItem.QuickActionType.LIST_DRUG_MEDICINE,
      -> baseActivity.startListServiceProduct(session)
      QuickActionItem.QuickActionType.ADD_SERVICE,
      QuickActionItem.QuickActionType.ADD_PRODUCT,
      QuickActionItem.QuickActionType.ADD_COURSE,
      QuickActionItem.QuickActionType.ADD_MENU,
      QuickActionItem.QuickActionType.ADD_ROOM_TYPE,
      -> baseActivity.startAddServiceProduct(session)
      QuickActionItem.QuickActionType.PLACE_APPOINTMENT -> baseActivity.startBookAppointmentConsult(session, false)
      QuickActionItem.QuickActionType.PLACE_CONSULT -> baseActivity.startBookAppointmentConsult(session, true)
      QuickActionItem.QuickActionType.ADD_PROJECT -> {
        if (session?.getStoreWidgets()?.equals(PremiumCode.PROJECTTEAM.value) == true) {
          baseActivity.startListProject(session)
        } else baseActivity.startListProjectAndTeams(session)
      }
      QuickActionItem.QuickActionType.ADD_TEAM_MEMBER -> {
        if (session?.getStoreWidgets()?.equals(PremiumCode.PROJECTTEAM.value) == true) {
          baseActivity.startListTeams(session)
        } else baseActivity.startListProjectAndTeams(session)
      }
      QuickActionItem.QuickActionType.UPLOAD_BROCHURE -> {
        if (session?.getStoreWidgets()?.equals(PremiumCode.BROCHURE.value) == true) {
          baseActivity.startAddDigitalBrochure(session)
        } else baseActivity.startListDigitalBrochure(session)
      }
      QuickActionItem.QuickActionType.POST_SEASONAL_OFFER -> baseActivity.startAddSeasonalOffer(session)
      QuickActionItem.QuickActionType.LIST_TOPPER -> baseActivity.startListToppers(session)
      QuickActionItem.QuickActionType.ADD_UPCOMING_BATCH -> baseActivity.startListBatches(session)
      QuickActionItem.QuickActionType.ADD_NEARBY_ATTRACTION -> baseActivity.startNearByView(session)
      QuickActionItem.QuickActionType.ADD_FACULTY_MEMBER -> baseActivity.startFacultyMember(session)

      QuickActionItem.QuickActionType.POST_STATUS_STORY,
      QuickActionItem.QuickActionType.ADD_SLIDER_BANNER,
      QuickActionItem.QuickActionType.PLACE_ORDER_BOOKING,
      QuickActionItem.QuickActionType.ADD_TABLE_BOOKING,
      QuickActionItem.QuickActionType.ADD_STAFF_MEMBER,
      QuickActionItem.QuickActionType.MAKE_ANNOUNCEMENT,
      -> {
        showShortToast("Coming soon...")
      }
    }
  }

  private fun clickBusinessUpdate(type: BusinessSetupHighData.BusinessClickEvent) {
    when (type) {
      BusinessSetupHighData.BusinessClickEvent.WEBSITE_VISITOR -> baseActivity.startSiteViewAnalytic(session, "UNIQUE")
      BusinessSetupHighData.BusinessClickEvent.ENQUIRIES -> baseActivity.startBusinessEnquiry(session)
      BusinessSetupHighData.BusinessClickEvent.ODER_APT -> baseActivity.startAptOrderSummary(session)
    }
  }

  private fun clickRoiSummary(type: RoiSummaryData.RoiType) {
    when (type) {
      RoiSummaryData.RoiType.ENQUIRY -> baseActivity.startBusinessEnquiry(session)
      RoiSummaryData.RoiType.TRACK_CALL -> baseActivity.startVmnCallCard(session)
      RoiSummaryData.RoiType.APT_ORDER -> baseActivity.startAptOrderSummary(session)
      RoiSummaryData.RoiType.CONSULTATION -> {
      }
      RoiSummaryData.RoiType.APT_ORDER_WORTH -> baseActivity.startRevenueSummary(session)
      RoiSummaryData.RoiType.COLLECTION_WORTH -> {
      }
    }
  }

  private fun clickGrowthStats(type: GrowthStatsData.GrowthType) {
    when (type) {
      GrowthStatsData.GrowthType.ALL_VISITS -> baseActivity.startSiteViewAnalytic(session, "TOTAL")
      GrowthStatsData.GrowthType.UNIQUE_VISITS -> baseActivity.startSiteViewAnalytic(session, "UNIQUE")
      GrowthStatsData.GrowthType.ADDRESS_NEWS -> baseActivity.startSiteViewAnalytic(session, "MAP_VISITS")
      GrowthStatsData.GrowthType.NEWSLETTER_SUBSCRIPTION -> baseActivity.startSubscriber(session)
      GrowthStatsData.GrowthType.SEARCH_QUERIES -> baseActivity.startSearchQuery(session)
    }
  }

  private fun getSummaryDetail(): SummaryEntity? {
    return SummaryEntity(session?.enquiryCount?.toIntOrNull() ?: 0, session?.subcribersCount?.toIntOrNull() ?: 0, session?.visitorsCount?.toIntOrNull() ?: 0, session?.visitsCount?.toIntOrNull() ?: 0)
  }

  private fun actionChannelClick(data: ChannelModel) {
    val title: String
    var website = ""
    if (data.isWhatsAppChannel().not()) {
      if (data.isGoogleSearch()) {
        title = "Website"
        website = session?.getDomainName(false) ?: ""
      } else {
        title = data.channelAccessToken?.userAccountName?.takeIf { it.isNotEmpty() } ?: data.getName()
        if (data.isTwitterChannel()) website = "https://twitter.com/${data.channelAccessToken?.userAccountName?.trim()}"
        else if (data.isFacebookPage()) website = "https://www.facebook.com/${data.channelAccessToken?.userAccountId?.trim()}"
      }
      bottomSheetWebView(title, website)
    }
  }

  private fun academyBannerBoostClick(data: DashboardAcademyBanner) {
    when {
      data.ctaFileLink.isNullOrEmpty().not() -> baseActivity.startDownloadUri(session, data.ctaFileLink?.trim()!!)
      data.ctaWebLink.isNullOrEmpty().not() -> baseActivity.startWebViewPageLoad(session, data.ctaWebLink?.trim()!!)
      data.ctaYoutubeLink.isNullOrEmpty().not() -> baseActivity.startYouTube(session, data.ctaYoutubeLink?.trim()!!)
    }
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.nestedScrollView?.gone()
    binding?.progress?.visible()
  }

  override fun hideProgress() {
    binding?.nestedScrollView?.visible()
    binding?.progress?.gone()
  }
}

private fun UserSessionManager.saveUserSummary(summary: SummaryEntity?) {
  enquiryCount = (summary?.noOfMessages ?: 0).toString()
  subcribersCount = summary?.noOfSubscribers.toString()
  visitorsCount = (summary?.noOfUniqueViews ?: 0).toString()
  visitsCount = (summary?.noOfViews ?: 0).toString()
}


fun UserSessionManager.getRequestMap(): Map<String, String> {
  val map = HashMap<String, String>()
  var startDate = ""
  val str = this.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON)
  if (str.isNullOrEmpty().not()) startDate = Date(getDateMillSecond(str!!)).parseDate(FORMAT_DD_MM_YYYY_N) ?: ""
  map["batchType"] = VisitsModelResponse.BatchType.yy.name
  map["startDate"] = startDate
  map["endDate"] = getCurrentDate().parseDate(FORMAT_DD_MM_YYYY_N) ?: ""
  map["clientId"] = clientId
  map["scope"] = if (this.iSEnterprise == "true") "Enterprise" else "Store"
  return map
}

fun UserSessionManager.getRequestFloat(): Map<String, String> {
  val map = HashMap<String, String>()
  map["clientId"] = clientId
  map["skipBy"] = "0"
  map["fpId"] = this.fPID!!
  return map
}

fun UserSessionManager?.checkIsPremiumUnlock(value: String?): Boolean {
  return (this?.getStoreWidgets()?.firstOrNull { it == value } != null)
}

fun getLocalSession(session: UserSessionManager): LocalSessionModel {
  var imageUri = session.getFPDetails(GET_FP_DETAILS_LogoUrl)
  if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) imageUri = BASE_IMAGE_URL + imageUri
  val city = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY)
  val country = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)
  val location = if (city.isNullOrEmpty().not() && country.isNullOrEmpty().not()) "$city, $country" else "$city$country"
  return LocalSessionModel(floatingPoint = session.fPID, contactName = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME), businessName = session.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME),
      businessImage = imageUri, location = location, websiteUrl = session.getDomainName(false),
      businessType = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY), primaryNumber = session.userPrimaryMobile,
      primaryEmail = session.fPEmail, fpTag = session.fpTag, experienceCode = session.fP_AppExperienceCode)
}

fun saveFirstLoad() {
  PreferencesUtils.instance.saveData(IS_FIRST_LOAD, true)
}

fun isFirstLoad(): Boolean {
  return PreferencesUtils.instance.getData(IS_FIRST_LOAD, false)
}