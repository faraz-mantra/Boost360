package com.dashboard.controller.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.bumptech.glide.Glide
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.FragmentType
import com.dashboard.constant.PreferenceConstant.CHANNEL_SHARE_URL
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.controller.DashboardActivity
import com.dashboard.controller.getDomainName
import com.dashboard.controller.startFragmentDashboardActivity
import com.dashboard.controller.ui.dialog.ProgressDashboardDialog
import com.dashboard.controller.ui.drScore.clickEventUpdateScoreN
import com.dashboard.databinding.FragmentDashboardV2Binding
import com.dashboard.databinding.ItemRecommendedTaskViewBinding
import com.dashboard.model.*
import com.dashboard.model.live.addOns.ManageBusinessData
import com.dashboard.model.live.addOns.ManageBusinessDataResponse
import com.dashboard.model.live.channel.*
import com.dashboard.model.live.dashboardBanner.*
import com.dashboard.model.live.drScore.DrScoreItem
import com.dashboard.model.live.drScore.DrScoreSetupData
import com.dashboard.model.live.drScore.DrScoreUiDataResponse
import com.dashboard.model.live.drScore.getDrScoreData
import com.dashboard.model.live.quickAction.QuickActionItem
import com.dashboard.model.live.quickAction.QuickActionResponse
import com.dashboard.model.live.shareUser.ShareUserDetailResponse
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.runOnUiThread
import com.framework.extensions.visible
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil.festivePosterName
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil.festivePosterVisibility
import com.framework.firebaseUtils.firestore.DrScoreModel
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.firebaseUtils.firestore.FirestoreManager.fpId
import com.framework.firebaseUtils.firestore.FirestoreManager.getDrScoreData
import com.framework.firebaseUtils.firestore.FirestoreManager.readDrScoreDocument
import com.framework.pref.*
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_LogoUrl
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_WEBSITE
import com.framework.utils.*
import com.framework.utils.DateUtils.parseDate
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.framework.webengageconstant.*
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.model.mapDetail.TOTAL_MAP_VISIT
import com.inventoryorder.model.mapDetail.VisitsModelResponse
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.ordersummary.SELLER_BUSINESS_REPORT
import com.inventoryorder.model.ordersummary.TOTAL_SELLER_SUMMARY
import com.inventoryorder.model.summary.*
import com.inventoryorder.model.summary.request.FilterBy
import com.inventoryorder.model.summary.request.QueryObject
import com.inventoryorder.model.summary.request.SellerSummaryRequest
import com.inventoryorder.model.summaryCall.CALL_BUSINESS_REPORT
import com.inventoryorder.model.summaryCall.CallSummaryResponse
import com.inventoryorder.rest.response.OrderSummaryResponse
import com.inventoryorder.utils.DynamicLinkParams
import com.inventoryorder.utils.DynamicLinksManager
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.channel.insights.ChannelInsightsResponse
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.getConnectedChannel
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.saveDataConnectedChannel
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.LocalSessionModel
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.VisitingCardSheet
import com.onboarding.nowfloats.ui.webview.WebViewBottomDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

const val IS_FIRST_LOAD = "isFirsLoad"
const val IS_DR_HIGH_DIALOG = "isDrHighDialog"

class DashboardFragmentV2 : AppBaseFragment<FragmentDashboardV2Binding, DashboardViewModel>(), RecyclerItemClickListener {

  private var deepLinkUtil: DeepLinkUtil? = null
  private var connectedChannels: ArrayList<String> = arrayListOf()
  private var session: UserSessionManager? = null
  private var adapterBusinessContent: AppBaseRecyclerViewAdapter<DrScoreSetupData>? = null
  private var adapterSocialMedia: AppBaseRecyclerViewAdapter<ChannelStatusData>? = null
  private var adapterPagerBusinessUpdate: AppBaseRecyclerViewAdapter<BusinessSetupHighData>? = null
  private var adapterRoi: AppBaseRecyclerViewAdapter<RoiSummaryData>? = null
  private var adapterGrowth: AppBaseRecyclerViewAdapter<GrowthStatsData>? = null
  private var adapterMarketBanner: AppBaseRecyclerViewAdapter<DashboardMarketplaceBanner>? = null
  private var adapterAcademy: AppBaseRecyclerViewAdapter<DashboardAcademyBanner>? = null
  private var actionItem: ArrayList<QuickActionItem>? = null
  private var adapterQuickAction: AppBaseRecyclerViewAdapter<QuickActionItem>? = null
  private var adapterBusinessData: AppBaseRecyclerViewAdapter<ManageBusinessData>? = null
  private var ctaFileLink: String? = null

  private val filterBusinessReport: FilterDateModel?
    get() {
      return FilterDateModel().getDateFilter(FILTER_BUSINESS_REPORT)
    }
  private val websiteFilter: FilterDateModel?
    get() {
      return FilterDateModel().getDateFilter(FILTER_WEBSITE_REPORT)
    }
  private val messageBusiness: String
    get() {
      return PreferencesUtils.instance.getData(CHANNEL_SHARE_URL, "") ?: ""
    }
  private val _connectedChannels: ArrayList<String>
    get() {
      return getConnectedChannel()
    }

  override fun getLayout(): Int {
    return R.layout.fragment_dashboard_v2
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    session = UserSessionManager(baseActivity)
    session?.let { deepLinkUtil = DeepLinkUtil(baseActivity, it) }
    showSimmer(isFirstLoad().not() || (baseActivity as? DashboardActivity)?.isLoadShimmer == true)
    setOnClickListener(
      binding?.profileView?.btnBusinessLogo, binding?.profileView?.btnNotofication, binding?.viewBusinessReport?.filterBusinessReport,
      binding?.growthStats?.filterWebsiteReport, binding?.drView?.retryDrScore, binding?.profileView?.btnVisitingCard, binding?.profileView?.txtDomainName,
      binding?.drView?.btnShowDigitalScore, binding?.viewBusinessReport?.viewEmptyEnquiries?.btnWhatsappEnquiries,
      binding?.viewBusinessReport?.viewEmptyEnquiries?.btnInstagramEnquiries, binding?.viewBusinessReport?.viewEmptyEnquiries?.btnTelegramEnquiries,
      binding?.viewBusinessReport?.viewEmptyEnquiries?.btnMessangerEnquiries, binding?.viewBusinessReport?.viewEmptyEnquiries?.btnOtherShareEnquiries,
      binding?.profileView?.btnFestive, binding?.recommendedTask?.btnShowAll
    )
    binding?.viewBusinessReport?.title?.text = getString(if (isDoctor(session?.fP_AppExperienceCode)) R.string.patient_enquiries else R.string.customer_enquiries)
    binding?.txtVersion?.text = "Version ${baseActivity.packageManager.getPackageInfo(baseActivity.packageName, 0).versionName}"
    getAllDashboardSummary()
    getPremiumBanner()
    getChannelAccessToken()
    displayFestiveButtonView()
    todoListItems()
  }

  override fun onResume() {
    super.onResume()
    setUserData()
    setSummaryDrScore()
    refreshData()
  }

  private fun todoListItems() {
    binding?.todoView?.pagerMyTodoList?.apply {
      val adapterTodo = AppBaseRecyclerViewAdapter(baseActivity, DemoToDoListCardsModel().getDataToDoList(), this@DashboardFragmentV2)
      offscreenPageLimit = 3
      adapter = adapterTodo
      binding?.todoView?.dotIndicator?.setViewPager2(this)
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }
  }

  private fun displayFestiveButtonView() {
    if (festivePosterVisibility()) {
      binding?.profileView?.btnFestive?.visibility = View.VISIBLE
      binding?.profileView?.customFestivalTv?.text = festivePosterName()?.capitalizeUtil()
    } else binding?.profileView?.btnFestive?.visibility = View.GONE
  }

  private fun getSocialMediaChannel() {
    val channelStatusList = getChannelStatus()
    updateUiSocialMedia(channelStatusList)
    viewModel?.getChannelsAccessTokenStatus(session?.fPID)?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) {
        val response = it as? ChannelAccessStatusResponse
        val channels = response?.channels.getChannelStatusList()
        channels.forEachIndexed { index, channelStatusData ->
          viewModel?.getChannelsInsight(session?.fPID, channelStatusData.accountType)?.observeOnce(viewLifecycleOwner) { it2 ->
            val response2 = it2 as? ChannelInsightsResponse
            if (response2?.isSuccess() == true) channelStatusData.insightsData = response2.data
            if (index + 1 == channels.size) {
              saveDataChannelStatus(channels)
              updateUiSocialMedia(channels)
            }
          }
        }
      }
    }
  }

  private fun updateUiSocialMedia(channels: ArrayList<ChannelStatusData>) {
    binding?.socialMedia?.apply {
      if (channels.isNotEmpty()) {
        mainView.visible()
        if (adapterSocialMedia == null) {
          rvMediaChannel.apply {
            adapterSocialMedia = AppBaseRecyclerViewAdapter(baseActivity, channels, this@DashboardFragmentV2)
            adapter = adapterSocialMedia
          }
        } else adapterSocialMedia?.notify(channels)
      } else mainView.gone()
    }
  }

  private fun getPremiumBanner() {
//    if (baseActivity.packageName.equals(APPLICATION_JIO_ID, ignoreCase = true)) {
//      setDataMarketBanner(ArrayList())
//      setDataRiaAcademy(ArrayList())
//    } else {
    setDataMarketBanner(getMarketPlaceBanners() ?: ArrayList())
    setDataRiaAcademy(getAcademyBanners() ?: ArrayList())
    viewModel?.getUpgradeDashboardBanner()?.observeOnce(viewLifecycleOwner) {
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
    }
//    }
  }

  private fun refreshData() {
    getSocialMediaChannel()
    setBusinessManageTask()
    getNotificationCount()
    refreshAllDashboardSummary()
  }

  private fun setSummaryDrScore(isLoadingShimmerDr: Boolean = false) {
    if (isLoadingShimmerDr) {
      WebEngageController.trackEvent(DIGITAL_READINESS_SCORE_RETRY, NO_EVENT_LABLE, NO_EVENT_VALUE)
      showSimmerDrScore(isSimmer = isLoadingShimmerDr)
    }
    if (getDrScoreData()?.drs_segment.isNullOrEmpty()) readDrScoreDocument()
    Handler(Looper.getMainLooper()).postDelayed({ runOnUiThread { setDrScoreData() } }, if (isLoadingShimmerDr) 2000 else 0)
  }

  private fun setDrScoreData() {
    viewModel?.getDrScoreUi(baseActivity)?.observeOnce(viewLifecycleOwner) {
      val response = it as? DrScoreUiDataResponse
      val drScoreData = getDrScoreData()
      val drScoreSetupList = response?.getActionItem(session?.fP_AppExperienceCode)?.let { it1 -> drScoreData?.getDrScoreData(it1) }
      if (response?.isSuccess() == true && drScoreSetupList.isNullOrEmpty().not()) {
        showSimmerDrScore()
        when {
          drScoreData?.getDrsTotal() ?: 0 >= 60 -> {
            setColorAndVisibilityDr(isEqual60 = true)
          }
          drScoreData?.getDrsTotal() ?: 0 >= 45 -> {
            setColorAndVisibilityDr()
            drDataSet(drScoreData)
          }
          else -> {
            setColorAndVisibilityDr(isLess45 = true)
            drDataSet(drScoreData)
            drScoreSetupList?.map { it1 -> it1.recyclerViewItemType = RecyclerViewItemType.BUSINESS_SETUP_ITEM_VIEW.getLayout() }
            binding?.drView?.pagerBusinessSetupLow?.apply {
              adapterBusinessContent = AppBaseRecyclerViewAdapter(baseActivity, drScoreSetupList!!, this@DashboardFragmentV2)
              offscreenPageLimit = 3
              adapter = adapterBusinessContent
              binding?.drView?.dotIndicator?.setViewPager2(this)
              setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
            }
          }
        }
      } else {
        if (baseActivity.packageName.equals(APPLICATION_JIO_ID, ignoreCase = true))
          (baseActivity as? DashboardActivity)?.changeTheme(R.color.colorPrimary, R.color.colorPrimary)
        showSimmerDrScore(retryView = true)
      }
    }
  }

  private fun drDataSet(drScoreData: DrScoreModel?) {
    binding?.drView?.txtReadinessScore?.text = "${drScoreData?.getDrsTotal() ?: 0}"
    binding?.drView?.progressBar?.progressDrawable = getProgressDrawable(drScoreData?.getDrsTotal() ?: 0)
    binding?.drView?.progressBar?.progress = drScoreData?.getDrsTotal() ?: 0
  }

  private fun getProgressDrawable(drsTotal: Int): Drawable? {
    return ContextCompat.getDrawable(
      baseActivity, when {
        drsTotal >= 100 -> R.drawable.seekbar_progress_dr_100
        drsTotal >= 80 -> R.drawable.seekbar_progress_dr_80
        drsTotal >= 60 -> R.drawable.seekbar_progress_dr_60
        drsTotal >= 40 -> R.drawable.seekbar_progress_dr_40
        else -> R.drawable.seekbar_progress_dr_20
      }
    )
  }

  private fun showSimmerDrScore(isSimmer: Boolean = false, retryView: Boolean = false) {
    binding?.drView?.highReadinessScoreView?.gone()
    binding?.drView?.lowReadinessScoreView?.gone()
    binding?.drView?.retryDrScore?.visibility = if (isSimmer || retryView) View.VISIBLE else View.GONE
    binding?.drView?.shimmerLoadDrView?.setBackgroundColor(getColor(if (isSimmer) R.color.placeholder_bg else android.R.color.transparent))
    binding?.drView?.shimmerLoadDrView?.apply { if (isSimmer) startShimmer() else stopShimmer() }
  }

  private fun setColorAndVisibilityDr(isLess45: Boolean = false, isEqual60: Boolean = false) {
    //setTopBackgroundView(isHighDrScore)
    binding?.drView?.highReadinessScoreView?.visibility = if (isEqual60) View.VISIBLE else View.GONE
    binding?.drView?.lowReadinessScoreView?.visibility = if (isEqual60) View.GONE else View.VISIBLE
    binding?.drView?.drViewItem?.visibility = if (isLess45) View.VISIBLE else View.GONE
    binding?.drView?.mainContent?.setCardBackgroundColor(getColor(if (isLess45) R.color.black_4a4a4a else R.color.white))
    binding?.drView?.bgView?.background = if (isLess45) null else ContextCompat.getDrawable(baseActivity, R.drawable.bg_white_grey_gradient)
    binding?.drView?.txtReadinessScore?.setTextColor(ContextCompat.getColor(baseActivity, if (isLess45) R.color.white else R.color.black_4a4a4a))
    binding?.drView?.percentSymbol?.setTextColor(ContextCompat.getColor(baseActivity, if (isLess45) R.color.white else R.color.black_4a4a4a))
    binding?.drView?.titleTxt?.setTextColor(ContextCompat.getColor(baseActivity, if (isLess45) R.color.white else R.color.black_4a4a4a))
    binding?.drView?.descTxt?.setTextColor(ContextCompat.getColor(baseActivity, if (isLess45) R.color.gray_light_1 else R.color.gray_828282))
  }

  private fun setTopBackgroundView(isHigh: Boolean) {
    if (baseActivity.packageName.equals(APPLICATION_JIO_ID, ignoreCase = true)) {
//      val colorD = if (isHigh) R.color.dashboard_high_score_pink else R.color.white_smoke_1
//      (baseActivity as? DashboardActivity)?.changeTheme(colorD, colorD, isHigh.not())
//      binding?.viewBgInner?.setTintColor(ContextCompat.getColor(baseActivity, colorD))
//      binding?.viewBgOuter?.setImageResource(if (isHigh) R.drawable.ic_fill_bg else R.drawable.ic_fill_bg_primary)
//      binding?.txtBusinessName?.setTextColor(ContextCompat.getColor(baseActivity, if (isHigh) R.color.white else R.color.black_4a4a4a))
//      binding?.txtDomainName?.apply {
//        setTextColor(ContextCompat.getColor(baseActivity, if (isHigh) R.color.white else R.color.black_4a4a4a))
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//          compoundDrawableTintList = ContextCompat.getColorStateList(baseActivity, if (isHigh) R.color.white else R.color.black_4a4a4a)
//        }
//      }
//      binding?.ctvImproveScore?.setTextColor(getColor(R.color.colorAccent))
//      binding?.btnVisitingCard?.background = ContextCompat.getDrawable(baseActivity, if (isHigh) R.drawable.ic_btn_round_blue_stroke else R.drawable.ic_btn_round_white_stroke)
//      binding?.txtVisitingCard?.setTextColor(ContextCompat.getColorStateList(baseActivity, if (isHigh) R.color.white else R.color.white))
//      binding?.txtNotification?.backgroundTintList = ContextCompat.getColorStateList(baseActivity, if (isHigh) R.color.accent_light_red else R.color.red_light_1)
    }
  }

  private fun setBusinessManageTask() {
    binding?.recommendedTask?.apply {
      viewModel?.getQuickActionData(baseActivity)?.observeOnce(viewLifecycleOwner) {
        val response = it as? QuickActionResponse
        val listAction = response?.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }
        if (response?.isSuccess() == true && listAction?.actionItem.isNullOrEmpty().not()) {
          actionItem = listAction?.actionItem
          this.btnQuickAction(actionItem!!.size > 8)
        } else showShortToast(baseActivity.getString(R.string.quick_action_data_error))
      }

    }
    binding?.manageBusiness?.apply {
      viewModel?.getBoostAddOnsTop(baseActivity)?.observeOnce(viewLifecycleOwner) {
        val response = it as? ManageBusinessDataResponse
        val dataAction = response?.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }
        if (dataAction?.actionItem.isNullOrEmpty().not()) {
          dataAction?.actionItem?.map { it1 ->
            if (it1.premiumCode.isNullOrEmpty().not()) {
              it1.isLock = session.checkIsPremiumUnlock(it1.premiumCode).not()
            }
          }
          if (adapterBusinessData == null) {
            rvManageBusiness.apply {
              adapterBusinessData = AppBaseRecyclerViewAdapter(baseActivity, dataAction?.actionItem!!, this@DashboardFragmentV2)
              adapter = adapterBusinessData
            }
          } else adapterBusinessData?.notify(dataAction?.actionItem!!)

        } else showShortToast(baseActivity.getString(R.string.manage_business_not_found))
      }
      btnShowAll.setOnClickListener {
        WebEngageController.trackEvent(BUSINESS_ADD_ONS_PAGE, ADD_ONS, session?.fpTag)
        startFragmentDashboardActivity(FragmentType.ALL_BOOST_ADD_ONS)
      }
    }
  }

  private fun ItemRecommendedTaskViewBinding.btnQuickAction(isMore6: Boolean) {
    this.maimShowAll.visibility = if (isMore6) View.VISIBLE else View.GONE
    val list = ArrayList(actionItem?.take(8) ?: arrayListOf())
    if (adapterQuickAction == null) {
      rvQuickAction.apply {
        adapterQuickAction = AppBaseRecyclerViewAdapter(baseActivity, list, this@DashboardFragmentV2)
        adapter = adapterQuickAction
      }
    } else adapterQuickAction?.notify(list)
  }

  private fun refreshAllDashboardSummary() {
    setBusinessSummary(
      getDrScoreData()?.getDrsTotal() ?: 0, OrderSummaryModel().getTotalOrder(TOTAL_SELLER_SUMMARY) ?: "0",
      SummaryEntity().getUserSummary(USER_BUSINESS_SUMMARY)
    )
    setRoiBusinessReport(
      OrderSummaryModel().getSellerSummary(SELLER_BUSINESS_REPORT),
      SummaryEntity().getTotalUserMessage(TOTAL_USER_MESSAGE).toString(),
      CallSummaryResponse().getCallSummary(CALL_BUSINESS_REPORT) ?: "0"
    )
    setWebsiteReport(
      SummaryEntity().getUserSummary(USER_WEBSITE_REPORT),
      VisitsModelResponse().getTotalOMapVisit(TOTAL_MAP_VISIT) ?: "0", null
    )
  }

  private fun getAllDashboardSummary() {
    apiBusinessSummary()
    var businessFilter = filterBusinessReport
    if (businessFilter == null) {
      businessFilter = FilterDateModel().getFilterDate().last()
      businessFilter.saveData(FILTER_BUSINESS_REPORT)
    }
    apiRoiBusinessReport(businessFilter)
    binding?.viewBusinessReport?.filterBusinessReport?.text = businessFilter.title
    var websiteFilter = websiteFilter
    if (websiteFilter == null) {
      websiteFilter = FilterDateModel().getFilterDate().last()
      websiteFilter.saveData(FILTER_WEBSITE_REPORT)
    }
    apiWebsiteReport(websiteFilter)
    binding?.growthStats?.filterWebsiteReport?.text = websiteFilter.title
  }

  private fun apiBusinessSummary() {
    viewModel?.getSellerSummaryV2_5(AppConstant.CLIENT_ID_ORDER, session?.fpTag, getRequestSellerSummary(null))?.observeOnce(viewLifecycleOwner) {
      val response1 = it as? OrderSummaryResponse
      if (response1?.isSuccess() == true && response1.Data != null) response1.Data?.saveTotalOrder(TOTAL_SELLER_SUMMARY)
      val scope = if (session?.iSEnterprise == "true") "1" else "0"
      viewModel?.getUserSummary(session?.fpTag, clientId, session?.fPParentId, scope)?.observeOnce(viewLifecycleOwner) { it1 ->
        val response2 = it1 as? UserSummaryResponse
        response2?.getSummary()?.saveData(USER_BUSINESS_SUMMARY)
        setBusinessSummary(getDrScoreData()?.getDrsTotal() ?: 0, response1?.Data?.getTotalOrders() ?: "0", response2?.getSummary())
      }
    }
  }

  private fun setBusinessSummary(drTotal: Int, totalOrder: String, summary: SummaryEntity?) {
    val data = BusinessSetupHighData().getData(
      drTotal, summary?.getNoOfUniqueViews() ?: "0", totalOrder,
      getCustomerTypeFromServiceCode(session?.fP_AppExperienceCode), summary?.getNoOfMessages() ?: "0"
    )
    data.map { it.recyclerViewItemType = RecyclerViewItemType.BUSINESS_SETUP_HIGH_ITEM_VIEW.getLayout() }
    if (adapterPagerBusinessUpdate == null) {
      binding?.drView?.pagerBusinessSetupHigh?.apply {
        adapterPagerBusinessUpdate = AppBaseRecyclerViewAdapter(baseActivity, data, this@DashboardFragmentV2)
        offscreenPageLimit = 3
        adapter = adapterPagerBusinessUpdate
        binding?.drView?.dotIndicatorBusinessHigh?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }
    } else adapterPagerBusinessUpdate?.notify(data)
  }

  private fun apiRoiBusinessReport(filterDate: FilterDateModel, isLoader: Boolean = false) {
    if (isLoader) showProgress()
    viewModel?.getSellerSummaryV2_5(AppConstant.CLIENT_ID_ORDER, session?.fpTag, getRequestSellerSummary(filterDate))?.observeOnce(viewLifecycleOwner) {
      val response1 = it as? OrderSummaryResponse
      if (response1?.isSuccess() == true && response1.Data != null) response1.Data?.saveData(SELLER_BUSINESS_REPORT)
      val scope = if (session?.iSEnterprise == "true") "1" else "0"
      viewModel?.getUserSummary(session?.fpTag, clientId, session?.fPParentId, scope, filterDate.startDate, filterDate.endDate)?.observeOnce(viewLifecycleOwner) { it1 ->
        val response2 = it1 as? UserSummaryResponse
        response2?.getSummary()?.saveTotalMessage(TOTAL_USER_MESSAGE)
        val identifierType = if (session?.iSEnterprise == "true") "MULTI" else "SINGLE"
        viewModel?.getUserCallSummary(clientId, session?.fPParentId, identifierType, filterDate.startDate, filterDate.endDate)?.observeOnce(viewLifecycleOwner) { it2 ->
          val response3 = it2 as? CallSummaryResponse
          response3?.saveData(CALL_BUSINESS_REPORT)
          setRoiBusinessReport(response1?.Data, response2?.getSummary()?.getNoOfMessages() ?: "0", response3?.getTotalCalls() ?: "0")
          if (isLoader) hideProgress()
        }
      }
    }
  }

  private fun setRoiBusinessReport(sellerOrder: OrderSummaryModel?, noOfMessage: String, totlaCalls: String) {
    val roiData = RoiSummaryData().getData(noOfMessage, totlaCalls, sellerOrder, getRoiSummaryType(session?.fP_AppExperienceCode))
    if (filterBusinessReport?.title?.equals(TILL_DATE) == true && roiData.isAllDataZero()) {
      binding?.viewBusinessReport?.viewEmptyEnquiries?.root?.visible()
      binding?.viewBusinessReport?.myEnquiriesView?.gone()
    } else {
      binding?.viewBusinessReport?.viewEmptyEnquiries?.root?.gone()
      binding?.viewBusinessReport?.myEnquiriesView?.visible()
    }
    roiData.map { it.recyclerViewItemType = RecyclerViewItemType.ROI_SUMMARY_ITEM_VIEW.getLayout() }
    if (adapterRoi == null) {
      binding?.viewBusinessReport?.rvRoiSummary?.apply {
        adapterRoi = AppBaseRecyclerViewAdapter(baseActivity, roiData, this@DashboardFragmentV2)
        adapter = adapterRoi
      }
    } else adapterRoi?.notify(roiData)
  }

  private fun apiWebsiteReport(filterDate: FilterDateModel, isLoader: Boolean = false) {
    if (isLoader) showProgress()
    if (isFirstLoad()) Handler(Looper.getMainLooper()).postDelayed({ runOnUiThread { showSimmer(false) } }, 2000)
    val scope = if (session?.iSEnterprise == "true") "1" else "0"
    viewModel?.getUserSummary(session?.fpTag, clientId, session?.fPParentId, scope, filterDate.startDate, filterDate.endDate)?.observeOnce(viewLifecycleOwner) { it1 ->
      val response1 = it1 as? UserSummaryResponse
      viewModel?.getSubscriberCount(session?.fpTag, clientId, filterDate.startDate, filterDate.endDate)?.observeOnce(viewLifecycleOwner) { it2 ->
        val subscriberCount = (it2.anyResponse as? Double)?.toInt() ?: 0
        val summary = response1?.getSummary()
        summary?.noOfSubscribers = subscriberCount
        summary?.saveData(USER_WEBSITE_REPORT)
        viewModel?.getMapVisits(session?.fpTag, session?.getRequestMap(filterDate.startDate ?: "", filterDate.endDate ?: ""))?.observeOnce(viewLifecycleOwner) { it3 ->
          val response3 = it3 as? VisitsModelResponse
          response3?.saveMapVisit(TOTAL_MAP_VISIT)
          viewModel?.getSearchAnalytics(session?.fPID, filterDate.startDate, filterDate.endDate)?.observeOnce(viewLifecycleOwner) {
            val countSearch = (it?.anyResponse as? String)?.toIntOrNull()
            setWebsiteReport(summary, response3?.getTotalCount() ?: "0", countSearch)
            if (isLoader) hideProgress()
            (baseActivity as? DashboardActivity)?.isLoadShimmer = false
            showSimmer(false)
            saveFirstLoad()
          }
        }
      }
    }
  }

  private fun setWebsiteReport(summary: SummaryEntity?, mapVisitCount: String, countSearch: Int?) {
    val countS = countSearch?.toString() ?: getNumberFormat((session?.searchCount?.toIntOrNull() ?: 0).toString())
    val growthStatsList = GrowthStatsData().getData(summary, mapVisitCount, countS)
    growthStatsList.map { it.recyclerViewItemType = RecyclerViewItemType.GROWTH_STATE_ITEM_VIEW.getLayout() }
    if (adapterGrowth == null) {
      binding?.growthStats?.rvGrowthState?.apply {
        adapterGrowth = AppBaseRecyclerViewAdapter(baseActivity, growthStatsList, this@DashboardFragmentV2)
        adapter = adapterGrowth
      }
    } else adapterGrowth?.notify(growthStatsList)
  }

  private fun setUserData() {
    binding?.profileView?.txtBusinessName?.text = session?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME)
    binding?.profileView?.txtDomainName?.text = fromHtml("<u>${session!!.getDomainName()}</u>")
    var imageLogoUri = session?.getFPDetails(GET_FP_DETAILS_LogoUrl)
    if (imageLogoUri.isNullOrEmpty().not() && imageLogoUri!!.contains("http").not()) {
      imageLogoUri = BASE_IMAGE_URL + imageLogoUri
    }

    binding?.profileView?.imgBusinessLogo?.let {
      if (imageLogoUri.isNullOrEmpty().not()) {
        baseActivity.glideLoad(mImageView = it, url = imageLogoUri!!, placeholder = R.drawable.gradient_white, isLoadBitmap = true)
      } else it.setImageResource(R.drawable.ic_add_logo_d)
    }
  }

  private fun setDataRiaAcademy(academyBanner: ArrayList<DashboardAcademyBanner>) {
    binding?.riaAcademyView?.pagerRiaAcademy?.apply {
      if (academyBanner.isNotEmpty()) {
        academyBanner.map { it.recyclerViewItemType = RecyclerViewItemType.RIA_ACADEMY_ITEM_VIEW.getLayout() }
        binding?.riaAcademyView?.root?.visible()
        if (adapterAcademy == null) {
          adapterAcademy = AppBaseRecyclerViewAdapter(baseActivity, academyBanner, this@DashboardFragmentV2)
          offscreenPageLimit = 3
          adapter = adapterAcademy
          binding?.riaAcademyView?.dotIndicatorAcademy?.setViewPager2(this)
          setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        } else adapterAcademy?.notify(academyBanner)
      } else binding?.riaAcademyView?.root?.gone()
    }
  }

  private fun getNotificationCount() {
    viewModel?.getNotificationCount(clientId, session?.fPID)?.observeOnce(viewLifecycleOwner) {
      val value = (it.anyResponse as? Double)?.toInt()
      if (it.isSuccess() && (value != null && value != 0)) {
        binding?.profileView?.txtNotification?.visibility = View.VISIBLE
        binding?.profileView?.txtNotification?.text = "$value+"
      } else binding?.profileView?.txtNotification?.visibility = View.INVISIBLE
    }
  }

  private fun setDataMarketBanner(marketBannerFilter: ArrayList<DashboardMarketplaceBanner>) {
    binding?.marketplaceOffer?.pagerBoostPremium?.apply {
      if (marketBannerFilter.isNotEmpty()) {
        marketBannerFilter.map { it.recyclerViewItemType = RecyclerViewItemType.BOOST_PREMIUM_ITEM_VIEW.getLayout() }
        binding?.marketplaceOffer?.root?.visible()
        if (adapterMarketBanner == null) {
          adapterMarketBanner = AppBaseRecyclerViewAdapter(baseActivity, marketBannerFilter, this@DashboardFragmentV2)
          offscreenPageLimit = 3
          adapter = adapterMarketBanner
          binding?.marketplaceOffer?.dotIndicatorPremium?.setViewPager2(this)
          setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        } else adapterMarketBanner?.notify(marketBannerFilter)
      } else binding?.marketplaceOffer?.root?.gone()
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.READING_SCORE_CLICK.ordinal -> {
        baseActivity.startReadinessScoreView(session, 0)
      }
      RecyclerViewActionType.BUSINESS_SETUP_SCORE_CLICK.ordinal -> baseActivity.startReadinessScoreView(session, position)
      RecyclerViewActionType.BUSINESS_SETUP_ADD_ITEM_START.ordinal -> {
        val dataDr = (item as? DrScoreSetupData)?.getDrScoreData()
        if (dataDr != null) {
          val type = DrScoreItem.DrScoreItemType.fromName(dataDr.id)
          if (type == DrScoreItem.DrScoreItemType.boolean_share_business_card) {
            if (messageBusiness.isNotEmpty()) businessWebsiteDetailMessage(messageBusiness, isBusinessCardShare = true)
            else getChannelAccessToken(true)
          } else clickEventUpdateScoreN(type, baseActivity, session)
        } else baseActivity.startReadinessScoreView(session, position)
      }
      RecyclerViewActionType.QUICK_ACTION_ITEM_CLICK.ordinal -> {
        val data = item as? QuickActionItem ?: return
        QuickActionItem.QuickActionType.from(data.quickActionType)?.let { quickActionClick(it) }
      }
      RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal -> {
        val data = item as? ManageBusinessData ?: return
        ManageBusinessData.BusinessType.fromName(data.businessType)?.let {
          com.dashboard.controller.ui.allAddOns.businessAddOnsClick(it, baseActivity, session)
        }
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
        marketPlaceBannerClick(data)
      }
      RecyclerViewActionType.PROMO_BOOST_ACADEMY_CLICK.ordinal -> {
        val data = item as? DashboardAcademyBanner ?: return
        academyBannerBoostClick(data)
      }
      RecyclerViewActionType.CHANNEL_ACTIVATE_CLICK.ordinal -> {
        val data = item as? ChannelStatusData ?: return
        session?.let { baseActivity.startDigitalChannel(it, data.accountType ?: "") }
      }
    }
  }

  private fun marketPlaceBannerClick(data: DashboardMarketplaceBanner) {
    if (data.ctaWebLink.isNullOrEmpty().not()) {
      if (data.ctaWebLink!!.contains("${baseActivity.packageName}.keyboard.home") || data.ctaWebLink!!.contains(getString(R.string.deep_base_url)) || data.ctaWebLink!!.contains(getString(R.string.deep_base_onelink_url))) {
        WebEngageController.trackEvent(BOOST_MARKETPLACE_BANNER_CLICK, DEEP_LINK, NO_EVENT_VALUE)
        val deepHashMap: HashMap<DynamicLinkParams, String> = DynamicLinksManager().getURILinkParams(Uri.parse(data.ctaWebLink))
        if (deepHashMap.containsKey(DynamicLinkParams.viewType)) {
          val viewType = deepHashMap[DynamicLinkParams.viewType]
          val buyItemKey = deepHashMap[DynamicLinkParams.buyItemKey]
          deepLinkUtil?.deepLinkPage(viewType ?: "", buyItemKey ?: "", false)
        } else deepLinkUtil?.deepLinkPage(data.ctaWebLink!!, "", false)
      } else {
        WebEngageController.trackEvent(BOOST_MARKETPLACE_BANNER_CLICK, WEB_LINK, NO_EVENT_VALUE)
        baseActivity.startWebViewPageLoad(session, data.ctaWebLink?.trim()!!)
      }
    } else {
      WebEngageController.trackEvent(BOOST_MARKETPLACE_BANNER_CLICK, FEATURE_ITEM_CLICK, NO_EVENT_VALUE)
      if (data.ctaFeatureKey.isNullOrEmpty().not()) {
        session?.let { baseActivity.initiateAddonMarketplace(it, false, "", data.ctaFeatureKey) }
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.viewBusinessReport?.filterBusinessReport -> bottomSheetFilter(BUSINESS_REPORT, filterBusinessReport)
      binding?.growthStats?.filterWebsiteReport -> bottomSheetFilter(WEBSITE_REPORT, websiteFilter)
      binding?.profileView?.btnNotofication -> session?.let { baseActivity.startNotification(it) }
      binding?.profileView?.btnBusinessLogo -> baseActivity.startBusinessLogo(session)//openDialogPicker()
      binding?.drView?.btnShowDigitalScore -> baseActivity.startReadinessScoreView(session, 0)
      binding?.profileView?.btnVisitingCard -> {
        if (messageBusiness.isNotEmpty() || _connectedChannels.isNotEmpty()) businessWebsiteDetailMessage(messageBusiness, isBusinessCardShare = true)
        else getChannelAccessToken(true)
      }
      binding?.drView?.retryDrScore -> setSummaryDrScore(true)
      binding?.profileView?.txtDomainName -> baseActivity.startWebViewPageLoad(session, session!!.getDomainName(false))
      binding?.viewBusinessReport?.viewEmptyEnquiries?.btnWhatsappEnquiries -> {
        if (messageBusiness.isNotEmpty()) businessWebsiteDetailMessage(messageBusiness, shareType = ShareType.WHATS_APP)
        else getChannelAccessToken(isEnquiriesShare = true, shareType = ShareType.WHATS_APP)
      }
      binding?.viewBusinessReport?.viewEmptyEnquiries?.btnInstagramEnquiries -> {
        if (messageBusiness.isNotEmpty()) businessWebsiteDetailMessage(messageBusiness, shareType = ShareType.INSTAGRAM)
        else getChannelAccessToken(isEnquiriesShare = true, shareType = ShareType.INSTAGRAM)
      }
      binding?.viewBusinessReport?.viewEmptyEnquiries?.btnTelegramEnquiries -> {
        if (messageBusiness.isNotEmpty()) businessWebsiteDetailMessage(messageBusiness, shareType = ShareType.TELEGRAM)
        else getChannelAccessToken(isEnquiriesShare = true, shareType = ShareType.TELEGRAM)
      }
      binding?.viewBusinessReport?.viewEmptyEnquiries?.btnMessangerEnquiries -> {
        if (messageBusiness.isNotEmpty()) businessWebsiteDetailMessage(messageBusiness, shareType = ShareType.MESSENGER)
        else getChannelAccessToken(isEnquiriesShare = true, shareType = ShareType.MESSENGER)
      }
//      binding?.viewBusinessReport?.viewEmptyEnquiries?.btnEmailEnquiries -> {
//        if (messageBusiness.isNotEmpty()) businessWebsiteDetailMessage(messageBusiness, shareType = ShareType.G_MAIL)
//        else getChannelAccessToken(isEnquiriesShare = true, shareType = ShareType.G_MAIL)
//      }
      binding?.viewBusinessReport?.viewEmptyEnquiries?.btnOtherShareEnquiries -> {
        if (messageBusiness.isNotEmpty()) businessWebsiteDetailMessage(messageBusiness, shareType = null)
        else getChannelAccessToken(isEnquiriesShare = true, shareType = null)
      }
      binding?.profileView?.btnFestive -> {
        baseActivity.startFestivePosterActivity()
      }
      binding?.recommendedTask?.btnShowAll -> {
        if (actionItem?.size ?: 0 > 8) {
          val isMore = (binding?.recommendedTask?.btnTitle?.text?.toString()?.equals(getString(R.string.show_more)) == true)
          quickActionClickView(isMore)
        }
      }
    }
  }

  private fun quickActionClickView(isMore: Boolean) {
    val list = ArrayList((if (isMore) actionItem else actionItem?.take(8)) ?: arrayListOf())
    adapterQuickAction?.notify(list)
    binding?.recommendedTask?.btnTitle?.text = getString(if (isMore) R.string.show_less else R.string.show_more)
    binding?.recommendedTask?.imgArrow?.rotation = if (isMore) 180F else 0F
    binding?.recommendedTask?.maimShowAll?.background = ContextCompat.getDrawable(baseActivity, if (isMore) R.drawable.ic_bg_white_n else R.drawable.ic_shadow_white)
    val params = binding?.recommendedTask?.maimShowAll?.layoutParams as? ConstraintLayout.LayoutParams
    params?.topToTop = if (isMore) ConstraintLayout.LayoutParams.UNSET else R.id.marginSpacer
    params?.bottomToBottom = if (isMore) ConstraintLayout.LayoutParams.UNSET else ConstraintLayout.LayoutParams.PARENT_ID
    params?.topToBottom = if (isMore) R.id.rv_quick_action else ConstraintLayout.LayoutParams.UNSET
    binding?.recommendedTask?.maimShowAll?.requestLayout()
  }

  private fun businessWebsiteDetailMessage(shareChannelText: String?, isBusinessCardShare: Boolean = false, shareType: ShareType? = null) {
    viewModel?.getBoostVisitingMessage(baseActivity)?.observeOnce(viewLifecycleOwner) {
      val response = it as? ShareUserDetailResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val messageDetail = response.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }?.message
        if (messageDetail.isNullOrEmpty().not()) {
          val lat = session?.getFPDetails(Key_Preferences.LATITUDE)
          val long = session?.getFPDetails(Key_Preferences.LONGITUDE)
          var location = ""
          val address = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
          if (lat != null && long != null) location = "${if (shareChannelText.isNullOrEmpty().not()) "\n\n" else ""}\uD83D\uDCCD *Find us on map: http://www.google.com/maps/place/$lat,$long*\n\n"
          if (address.isNullOrEmpty().not()) location = "$location Address: $address\n\n"
          val txt = String.format(
            messageDetail!!, session?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME) ?: "",
            session?.getDomainName(false) ?: "", shareChannelText, location
          )
          if (isBusinessCardShare) visitingCard(txt) else baseActivity.shareViaAnyApp(shareType, txt)
        }
      } else {
        if (isBusinessCardShare) visitingCard(getString(R.string.my_business_card)) else showShortToast("Business detail getting error!")
      }
    }
  }

  private fun visitingCard(shareChannelText: String) {
    session?.let {
      val dialogCard = VisitingCardSheet()
      dialogCard.setData(getLocalSession(it), shareChannelText)
      dialogCard.show(this@DashboardFragmentV2.parentFragmentManager, VisitingCardSheet::class.java.name)
    }
  }

  private fun bottomSheetWebView(title: String, domainUrl: String) {
    val webViewBottomDialog = WebViewBottomDialog()
    webViewBottomDialog.setData(title, domainUrl)
    webViewBottomDialog.show(this@DashboardFragmentV2.parentFragmentManager, WebViewBottomDialog::class.java.name)
  }

  private fun quickActionClick(type: QuickActionItem.QuickActionType) {
    when (type) {
      QuickActionItem.QuickActionType.POST_NEW_UPDATE -> baseActivity.startPostUpdate(session)
      QuickActionItem.QuickActionType.ADD_PHOTO_GALLERY -> baseActivity.startAddImageGallery(session)
      QuickActionItem.QuickActionType.ADD_TESTIMONIAL -> baseActivity.startTestimonial(session, true)
      QuickActionItem.QuickActionType.ADD_CUSTOM_PAGE -> baseActivity.startCustomPage(session, true)
      QuickActionItem.QuickActionType.ADD_STAFF_PROFILE -> baseActivity.startAddStaff(session)
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
      -> showShortToast(getString(R.string.coming_soon))
      else -> {}
    }
  }

  private fun clickBusinessUpdate(type: BusinessSetupHighData.BusinessClickEvent) {
    when (type) {
      BusinessSetupHighData.BusinessClickEvent.WEBSITE_VISITOR -> baseActivity.startSiteViewAnalytic(session, "UNIQUE", WEBSITE_REPORT_UNIQUE_VISITS_CLICK)
      BusinessSetupHighData.BusinessClickEvent.ENQUIRIES -> baseActivity.startBusinessEnquiry(session)
      BusinessSetupHighData.BusinessClickEvent.ODER_APT -> baseActivity.startAptOrderSummary(session)
    }
  }

  private fun clickRoiSummary(type: RoiSummaryData.RoiType) {
    when (type) {
      RoiSummaryData.RoiType.ENQUIRY -> baseActivity.startBusinessEnquiry(session)
      RoiSummaryData.RoiType.TRACK_CALL -> baseActivity.startVmnCallCard(session)
      RoiSummaryData.RoiType.APT_ORDER -> baseActivity.startAptOrderSummary(session)
      RoiSummaryData.RoiType.CONSULTATION -> showShortToast(getString(R.string.video_consultation_analytics_coming_soon))
      RoiSummaryData.RoiType.APT_ORDER_WORTH -> {
        //baseActivity.startRevenueSummary(session)
        showShortToast(getString(R.string.collection_analytics_coming_soon))
      }
      RoiSummaryData.RoiType.COLLECTION_WORTH -> showShortToast(getString(R.string.collection_analytics_coming_soon))
    }
  }

  private fun clickGrowthStats(type: GrowthStatsData.GrowthType) {
    when (type) {
      GrowthStatsData.GrowthType.ALL_VISITS -> baseActivity.startSiteViewAnalytic(session, "TOTAL", WEBSITE_REPORT_ALL_VISITS_CLICK)
      GrowthStatsData.GrowthType.UNIQUE_VISITS -> baseActivity.startSiteViewAnalytic(session, "UNIQUE", WEBSITE_REPORT_UNIQUE_VISITS_CLICK)
      GrowthStatsData.GrowthType.ADDRESS_NEWS -> baseActivity.startSiteViewAnalytic(session, "MAP_VISITS", WEBSITE_REPORT_ADDRESS_VISITS_CLICK)
      GrowthStatsData.GrowthType.NEWSLETTER_SUBSCRIPTION -> baseActivity.startSubscriber(session)
      //GrowthStatsData.GrowthType.SEARCH_QUERIES -> baseActivity.startSearchQuery(session)
      else -> {}
    }
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
    val loader = ProgressDashboardDialog.newInstance()
    when {
      data.ctaFileLink.isNullOrEmpty().not() -> {
        WebEngageController.trackEvent(BOOST_ACADEMY_BANNER_CLICK, FILE_LINK, NO_EVENT_VALUE)
        this.ctaFileLink = data.ctaFileLink
        if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
          ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {
          requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        } else {
          loader.setData(R.raw.download_gif, resources.getString(R.string.download_file_banner))
          loader.showProgress(baseActivity.supportFragmentManager)
          baseActivity.startDownloadUri(data.ctaFileLink?.trim()!!)
          Handler().postDelayed({ loader.hideProgress() }, 1000)
        }
      }
      data.ctaWebLink.isNullOrEmpty().not() -> {
        WebEngageController.trackEvent(BOOST_ACADEMY_BANNER_CLICK, WEB_LINK, NO_EVENT_VALUE)
        loader.setData(
          R.raw.activity_browser_gif,
          resources.getString(R.string.opening_browser_banner)
        )
        loader.showProgress(baseActivity.supportFragmentManager)
        Handler().postDelayed({
          baseActivity.startWebViewPageLoad(session, data.ctaWebLink?.trim()!!)
          loader.hideProgress()
        }, 1000)
      }
      data.ctaYoutubeLink.isNullOrEmpty().not() -> {
        WebEngageController.trackEvent(BOOST_ACADEMY_BANNER_CLICK, YOUTUBE_LINK, NO_EVENT_VALUE)
        loader.setData(R.raw.video_gif, resources.getString(R.string.taking_video_banner))
        loader.showProgress(baseActivity.supportFragmentManager)
        Handler().postDelayed({
          baseActivity.startYouTube(session, data.ctaYoutubeLink?.trim()!!)
          loader.hideProgress()
        }, 1000)
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      100 -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (ctaFileLink.isNullOrEmpty().not()) {
            val loader = ProgressDashboardDialog.newInstance()
            loader.setData(R.raw.download_gif, resources.getString(R.string.download_file_banner))
            loader.showProgress(baseActivity.supportFragmentManager)
            baseActivity.startDownloadUri(ctaFileLink?.trim()!!)
            Handler().postDelayed({ loader.hideProgress() }, 1000)
          }
        } else showShortToast("Permission denied to read your External storage")
        return
      }
    }
  }


  private fun showSimmer(isSimmer: Boolean) {
//    binding?.root?.apply {
//      if (isSimmer) {
//        binding?.progressSimmer?.parentShimmerLayout?.visible()
//        binding?.progressSimmer?.parentShimmerLayout?.startShimmer()
//        binding?.nestedScrollView?.gone()
//      } else {
//        binding?.nestedScrollView?.visible()
//        binding?.progressSimmer?.parentShimmerLayout?.gone()
//        binding?.progressSimmer?.parentShimmerLayout?.stopShimmer()
//      }
//    }
  }

  private fun getChannelAccessToken(isBusinessCardShare: Boolean = false, isEnquiriesShare: Boolean = false, shareType: ShareType? = null) {
    if (isBusinessCardShare || isEnquiriesShare) showProgress()
    viewModel?.getChannelsAccessTokenStatus(session?.fPID)?.observeOnce(this) {
      var urlString = ""
      if (it.isSuccess()) {
        val response = it as? ChannelAccessStatusResponse
        connectedChannels.clear()
        if (response?.channels?.facebookpage?.status == CHANNEL_STATUS_SUCCESS) {
          urlString = "\n⚡ *Facebook: https://www.facebook.com/${response.channels?.facebookpage?.account?.accountId}*"
          connectedChannels.add(ChannelsType.AccountType.facebookpage.name)
        }
        if (response?.channels?.twitter?.status == CHANNEL_STATUS_SUCCESS) {
          urlString += "\n⚡ *Twitter: https://twitter.com/${response.channels?.twitter?.account?.accountName?.trim()}*"
          connectedChannels.add(ChannelsType.AccountType.twitter.name)
        }

        if (response?.channels?.googlemybusiness?.status == CHANNEL_STATUS_SUCCESS) {
          connectedChannels.add(ChannelsType.AccountType.googlemybusiness.name)
        }
        if (response?.channels?.facebookshop?.status == CHANNEL_STATUS_SUCCESS) {
          connectedChannels.add(ChannelsType.AccountType.facebookshop.name)
        }
      }
      getWhatsAppData(urlString, isBusinessCardShare, isEnquiriesShare, shareType)
    }
  }

  private fun getWhatsAppData(urlString: String, isBusinessCardShare: Boolean = false, isEnquiriesShare: Boolean = false, shareType: ShareType? = null) {
    var urlStringN = urlString
    viewModel?.getWhatsappBusiness(request = session?.fpTag, auth = WA_KEY)?.observeOnce(this) {
      if (isBusinessCardShare || isEnquiriesShare) hideProgress()
      if (it.isSuccess()) {
        val response = ((it as? ChannelWhatsappResponse)?.Data)?.firstOrNull()
        if (response != null && response.active_whatsapp_number.isNullOrEmpty().not()) {
          urlStringN += "\n⚡ *WhatsApp: https://wa.me/${response.getNumberPlus91()}*"
          connectedChannels.add(ChannelsType.AccountType.WAB.name)
        }
      }
      val otherWebsite = session?.getFPDetails(GET_FP_DETAILS_WEBSITE)
      if (otherWebsite.isNullOrEmpty().not()) urlStringN += "\n⚡ *Other Website: $otherWebsite*"
      saveDataConnectedChannel(connectedChannels)
      if (session?.userPrimaryMobile.isNullOrEmpty().not()) urlStringN += "\n\uD83D\uDCDE *Call: ${session?.userPrimaryMobile}*"
      PreferencesUtils.instance.saveData(CHANNEL_SHARE_URL, urlStringN)
      if (isBusinessCardShare) businessWebsiteDetailMessage(urlStringN, isBusinessCardShare = true)
      else if (isEnquiriesShare) businessWebsiteDetailMessage(urlStringN, shareType = shareType)
    }
  }

  private fun bottomSheetFilter(type: String, dateFilter: FilterDateModel?) {
    val filterBottomSheet = FilterBottomSheet()
    filterBottomSheet.setData(dateFilter)
    filterBottomSheet.onClicked = { apiFilterCall(it, type) }
    filterBottomSheet.show(this@DashboardFragmentV2.parentFragmentManager, FilterBottomSheet::class.java.name)
  }

  private fun apiFilterCall(filterDate: FilterDateModel, type: String) {
    when (type) {
      BUSINESS_REPORT -> {
        filterDate.saveData(FILTER_BUSINESS_REPORT)
        apiRoiBusinessReport(filterDate, true)
        binding?.viewBusinessReport?.filterBusinessReport?.text = filterDate.title
      }
      WEBSITE_REPORT -> {
        filterDate.saveData(FILTER_WEBSITE_REPORT)
        apiWebsiteReport(filterDate, true)
        binding?.growthStats?.filterWebsiteReport?.text = filterDate.title
      }
    }
    WebEngageController.trackEvent(CLICK_DATE_RANGE, CLICK, TO_BE_ADDED)
  }

  override fun onStop() {
    super.onStop()
    FirestoreManager.listener = null
  }

  override fun onStart() {
    super.onStart()
    FirestoreManager.listener = { setSummaryDrScore(true) }
  }
}

fun UserSessionManager.getRequestMap(startDate: String, endDate: String): Map<String, String> {
  val map = HashMap<String, String>()
  map["batchType"] = VisitsModelResponse.BatchType.yy.name
  map["startDate"] = if (startDate.isNotEmpty()) startDate else DateUtils.getCurrentDate().parseDate(DateUtils.FORMAT_YYYY_MM_DD) ?: ""
  map["endDate"] = if (endDate.isNotEmpty()) endDate else DateUtils.getAmountYearDate(-5).parseDate(DateUtils.FORMAT_YYYY_MM_DD) ?: ""
  map["clientId"] = clientId
  map["scope"] = if (this.iSEnterprise == "true") "Enterprise" else "Store"
  return map
}

fun UserSessionManager?.checkIsPremiumUnlock(value: String?): Boolean {
  return (this?.getStoreWidgets()?.firstOrNull { it == value } != null)
}

fun getLocalSession(session: UserSessionManager): LocalSessionModel {
  var imageUri = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl)
  if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) imageUri = BASE_IMAGE_URL + imageUri
  val city = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY)
  val country = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)
  val location = if (city.isNullOrEmpty().not() && country.isNullOrEmpty().not()) "$city, $country" else "$city$country"
  return LocalSessionModel(
    floatingPoint = session.fPID,
    contactName = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME),
    businessName = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME),
    businessImage = imageUri,
    location = location,
    websiteUrl = session.getDomainName(false),
    businessType = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY),
    primaryNumber = session.userPrimaryMobile,
    primaryEmail = session.fPEmail,
    fpTag = session.fpTag,
    experienceCode = session.fP_AppExperienceCode
  )
}

fun saveFirstLoad() {
  PreferencesUtils.instance.saveData(IS_FIRST_LOAD, true)
}

fun isFirstLoad(): Boolean {
  return PreferencesUtils.instance.getData(IS_FIRST_LOAD, false)
}

fun getRequestSellerSummary(filterDate: FilterDateModel?): SellerSummaryRequest {
  if (filterDate?.startDate.isNullOrEmpty()) return SellerSummaryRequest(filterBy = ArrayList())
  val request = SellerSummaryRequest()
  val queryObject = arrayListOf(
    QueryObject(key = QueryObject.keys.CreatedOn.name, value = filterDate?.startDate, queryOperator = QueryObject.Operator.GTE.name),
    QueryObject(key = QueryObject.keys.CreatedOn.name, value = filterDate?.endDate, queryOperator = QueryObject.Operator.LTE.name)
  )
  request.filterBy = arrayListOf(FilterBy(queryConditionType = FilterBy.ConditionType.AND.name, queryObject))
  return request
}