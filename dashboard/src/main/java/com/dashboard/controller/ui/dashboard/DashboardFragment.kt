package com.dashboard.controller.ui.dashboard

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.appservice.model.onboardingUpdate.OnBoardingUpdateModel
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.FragmentType
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.controller.DashboardActivity
import com.dashboard.controller.getDomainName
import com.dashboard.controller.startFragmentDashboardActivity
import com.dashboard.databinding.FragmentDashboardBinding
import com.dashboard.model.*
import com.dashboard.model.live.addOns.ManageBusinessData
import com.dashboard.model.live.addOns.ManageBusinessDataResponse
import com.dashboard.model.live.premiumBanner.*
import com.dashboard.model.live.quickAction.QuickActionData
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
import com.framework.utils.DateUtils.FORMAT_DD_MM_YYYY_N
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.getDateMillSecond
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.fromHtml
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.inventoryorder.model.floatMessage.MessageModel
import com.inventoryorder.model.mapDetail.VisitsModelResponse
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.summary.SummaryEntity
import com.inventoryorder.model.summary.UserSummaryResponse
import com.inventoryorder.model.summaryCall.CallSummaryResponse
import com.inventoryorder.rest.response.OrderSummaryResponse
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.model.channel.respose.NFXAccessToken
import com.onboarding.nowfloats.rest.response.category.ResponseDataCategory
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import com.onboarding.nowfloats.rest.response.channel.ChannelsAccessTokenResponse
import com.onboarding.nowfloats.ui.webview.WebViewBottomDialog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DashboardFragment : AppBaseFragment<FragmentDashboardBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var isRecreate: Boolean? = null
  private var isHigh = false
  private var adapterBusinessContent: AppBaseRecyclerViewAdapter<BusinessContentSetupData>? = null
  private var channelAdapter: AppBaseRecyclerViewAdapter<ChannelData>? = null
  private var adapterPagerBusinessUpdate: AppBaseRecyclerViewAdapter<BusinessSetupHighData>? = null
  private var adapterRoi: AppBaseRecyclerViewAdapter<RoiSummaryData>? = null
  private var adapterGrowth: AppBaseRecyclerViewAdapter<GrowthStatsData>? = null
  private var adapterMarketBanner: AppBaseRecyclerViewAdapter<PromoAcademyBanner>? = null
  private var adapterAcademy: AppBaseRecyclerViewAdapter<PromoAcademyBanner>? = null
  private var siteMeterData: SiteMeterScoreDetails? = null
  private var quickActionPosition = 0
  private var isFirsLoad = true
  private var isExpendCard = false

  override fun getLayout(): Int {
    return R.layout.fragment_dashboard
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    setOnClickListener(binding?.btnVisitingCardUp, binding?.btnVisitingCardDown, binding?.btnShowDigitalScore, binding?.btnDigitalChannel, binding?.btnBusinessLogo, binding?.txtDomainName, binding?.btnNotofication, binding?.btnShareWhatsapp, binding?.btnShareMore)
    val versionName: String = baseActivity.packageManager.getPackageInfo(baseActivity.packageName, 0).versionName
    binding?.txtVersion?.text = "Version $versionName"
    getCategoryData()
    apiSellerSummary()
    getPremiumBanner()
    WebEngageController.trackEvent("Dashboard Home Page", "pageview", session?.fpTag)
  }

  private fun getPremiumBanner() {
    setDataMarketBanner(PremiumFeatureData().getMarketPlaceBanners() ?: ArrayList())
    setDataRiaAcademy(PremiumFeatureData().getAcademyBanners() ?: ArrayList())
    viewModel?.getUpgradePremiumBanner()?.observeOnce(viewLifecycleOwner, {
      val response = it as? UpgradePremiumFeatureResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val data = response.data?.get(0)
        val promoBannersFilter = (data?.promoBanners ?: ArrayList()).marketBannerFilter(session)
        saveDataMarketPlace(promoBannersFilter)
        setDataMarketBanner(promoBannersFilter)
        val academyBannerFilter = (data?.academyBanner ?: ArrayList()).marketBannerFilter(session)
        saveDataAcademy(academyBannerFilter)
        setDataRiaAcademy(academyBannerFilter)
      }
    })
  }

  override fun onResume() {
    super.onResume()
    baseActivity.runOnUiThread { getFloatMessage() }
  }

  private fun getFloatMessage() {
    if (isFirsLoad) session?.siteMeterData { it?.let { it1 -> refreshData(it1) } }
    binding?.progress?.visible()
    viewModel?.getBizFloatMessage(session!!.getRequestFloat())?.observeOnce(this, {
      isFirsLoad = false
      if (it?.isSuccess() == true) (it as? MessageModel)?.saveData()
      session?.siteMeterData { it1 -> it1?.let { it2 -> refreshData(it2) } }
      binding?.progress?.gone()
    })
  }

  private fun refreshData(siteMeterData: SiteMeterScoreDetails) {
    this.siteMeterData = siteMeterData
    (baseActivity as? DashboardActivity)?.setPercentageData(siteMeterData.siteMeterTotalWeight)
    isHigh = (siteMeterData.siteMeterTotalWeight >= 80)
    if (isExpendCard.not()) visitingCardShowHide(isDown = true, isStart = true)
    if (isRecreate != null && this.isHigh != this.isRecreate) {
      getCategoryData()
      apiSellerSummary()
      getPremiumBanner()
      setUserBusinessAllData(siteMeterData)
    } else setUserBusinessAllData(siteMeterData)
    isRecreate = this.isHigh
  }

  private fun setUserBusinessAllData(siteMeterData: SiteMeterScoreDetails) {
    setUserData()
    setRecBusinessManageTask()
    getNotificationCount()
    getSiteMeter(siteMeterData)
    setDataSellerSummary(OrderSummaryModel().getSellerSummary(), getSummaryDetail(), CallSummaryResponse().getCallSummary())
  }

  private fun visitingCardShowHide(isDown: Boolean, isStart: Boolean = false) {
    Timer().schedule(when {
      isStart -> 0
      isDown -> 60
      isHigh -> 200
      else -> 150
    }) {
      binding?.viewDigitalScore?.post {
        binding?.viewDigitalScore?.elevation = resources.getDimension(if (isDown) R.dimen.size_2 else R.dimen.size_0)
        binding?.viewAllBusinessContact?.visibility = if (isDown) View.GONE else View.VISIBLE
        binding?.viewVisitingCardProduct?.visibility = if (isDown) View.VISIBLE else View.GONE
        if (isHigh) {
          binding?.viewUpBusinessShadow?.background = if (isDown) ContextCompat.getDrawable(baseActivity, R.drawable.up_shadow_d) else null
          binding?.viewBusinessBgScore?.background = if (isDown) null else ContextCompat.getDrawable(baseActivity, R.drawable.ic_bg_dark_white_vertical)
        }
      }
    }
    binding?.viewDigitalScore?.animateViewTopPadding(isDown, isStart)
  }

  private fun getSiteMeter(siteMeterData: SiteMeterScoreDetails) {
    val listDigitalScore = siteMeterData.getListDigitalScore()
    if (isHigh) {
      binding?.viewLowDigitalReadiness?.gone()
      binding?.viewLowTaskManageBusiness?.gone()
      binding?.viewHighDigitalReadiness?.visible()
    } else {
      binding?.txtReadinessScore?.text = "${siteMeterData.siteMeterTotalWeight}"
      binding?.progressScore?.progress = siteMeterData.siteMeterTotalWeight
      binding?.viewHighDigitalReadiness?.gone()
      binding?.viewLowDigitalReadiness?.visible()
      binding?.viewLowTaskManageBusiness?.visible()
      val listContent = ArrayList(listDigitalScore.map { it.recyclerViewItemType = RecyclerViewItemType.BUSINESS_SETUP_ITEM_VIEW.getLayout();it })
      binding?.pagerBusinessSetupLow?.apply {
        binding?.motionOne?.transitionToStart()
        adapterBusinessContent = AppBaseRecyclerViewAdapter(baseActivity, listContent, this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterBusinessContent
        postInvalidateOnAnimation()
        binding?.dotIndicator?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
          override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding?.motionOne?.loadLayoutDescription(takeIf { position == 0 }?.let { R.xml.fragment_dashboard_scene } ?: 0)
          }
        })
        binding?.motionOne?.loadLayoutDescription(R.xml.fragment_dashboard_scene)
        binding?.motionOne?.transitionToStart()
      }
      baseActivity.setGifAnim(binding?.missingDetailsGif!!, R.raw.ic_missing_setup_gif_d, R.drawable.ic_custom_page_d)
      baseActivity.setGifAnim(binding?.arrowLeftGif!!, R.raw.ic_arrow_left_gif_d, R.drawable.ic_arrow_right_14_d)
    }

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
    (if (isHigh) binding?.highRecommendedTask else binding?.lowRecommendedTask)?.apply {
      viewModel?.getQuickActionData(baseActivity)?.observeOnce(viewLifecycleOwner, {
        val response = it as? QuickActionResponse
        val listAction = response?.data?.firstOrNull { it1 -> it1.type?.toUpperCase(Locale.ROOT) == session?.fP_AppExperienceCode?.toUpperCase(Locale.ROOT) }
        if (response?.isSuccess() == true && listAction?.actionItem.isNullOrEmpty().not()) {
          val position = quickActionPosition
          pagerQuickAction.apply {
            val adapterQuickAction = AppBaseRecyclerViewAdapter(baseActivity, listAction?.actionItem!!, this@DashboardFragment)
            offscreenPageLimit = 3
            adapter = adapterQuickAction
            dotIndicatorAction.setViewPager2(this)
            post { setCurrentItem(position, false) }
            setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
              override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                quickActionPosition = position
              }
            })
          }
        } else showShortToast(baseActivity.getString(R.string.quick_action_data_error))
      })

    }
    (if (isHigh) binding?.highManageBusiness else binding?.lowManageBusiness)?.apply {
      title.text = if (getRoiSummaryType(session?.fP_AppExperienceCode) == "DOC") baseActivity.getString(R.string.manage_your_clinic) else baseActivity.getString(R.string.manage_your_business)
      rvManageBusiness.apply {
        viewModel?.getBoostAddOnsTop(baseActivity)?.observeOnce(viewLifecycleOwner, {
          val response = it as? ManageBusinessDataResponse
          val dataAction = response?.data?.firstOrNull { it1 -> it1.type?.toUpperCase(Locale.ROOT) == session?.fP_AppExperienceCode?.toUpperCase(Locale.ROOT) }
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
    if (isHigh) {
      binding?.viewHighSummaryBottom?.apply { visible() }
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
    } else binding?.viewHighSummaryBottom?.apply { gone() }
  }

  private fun setViewChannels(channels: ArrayList<ChannelModel>?) {
    val list = ArrayList<ChannelData>()
    channels?.forEach { list.add(ChannelData(it)) }
    if (channelAdapter == null) {
      binding?.rvChannelList?.apply {
        channelAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this@DashboardFragment)
        adapter = channelAdapter
      }
    } else channelAdapter?.notify(list)
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

  private fun setDataRiaAcademy(academyBanner: ArrayList<PromoAcademyBanner>) {
    binding?.pagerRiaAcademy?.apply {
      if (academyBanner.isNotEmpty()) {
        academyBanner.map { it.recyclerViewItemType = RecyclerViewItemType.RIA_ACADEMY_ITEM_VIEW.getLayout() }
        binding?.riaAcademyView?.visible()
        if (adapterAcademy == null) {
          adapterAcademy = AppBaseRecyclerViewAdapter(baseActivity, academyBanner, this@DashboardFragment)
          offscreenPageLimit = 3
//          clipToPadding = false
//          setPadding(37, 0, 37, 0)
          adapter = adapterAcademy
          setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        } else adapterAcademy?.notify(academyBanner)
      } else binding?.riaAcademyView?.gone()
    }
  }

  private fun setDataMarketBanner(marketBannerFilter: ArrayList<PromoAcademyBanner>) {
    if (isHigh) {
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
  }

  private fun getCategoryData() {
    val data = CategoryDataModel().getCategoryChannelData()
    if (data != null && data.channels.isNullOrEmpty().not()) {
      setViewChannels(data.channels)
    } else showProgress()
    viewModel?.getCategories(baseActivity)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        val categoryData = (it as? ResponseDataCategory)?.data?.singleOrNull { c -> c.experienceCode() == session?.fP_AppExperienceCode }
        if (categoryData != null) {
          getChannelAccessToken(categoryData)
        } else showErrorChannel("Category not found.")
      } else showErrorChannel(it.message())
    })
  }

  private fun getChannelAccessToken(categoryData: CategoryDataModel) {
    viewModel?.getChannelsAccessToken(session?.fPID)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        setDataRequestChannels(categoryData, (it as? ChannelsAccessTokenResponse)?.NFXAccessTokens)
      } else setDataRequestChannels(categoryData, ArrayList())
    })
  }

  private fun setDataRequestChannels(categoryData: CategoryDataModel, channelsAccessToken: List<NFXAccessToken>?) {
    if (channelsAccessToken.isNullOrEmpty().not()) {
      channelsAccessToken?.forEach {
        var data: ChannelAccessToken? = null
        when (it.type()) {
          ChannelAccessToken.AccessTokenType.facebookpage.name,
          ChannelAccessToken.AccessTokenType.twitter.name,
          -> {
            if (it.isValidType()) {
              data = ChannelAccessToken(type = it.type(), userAccessTokenKey = it.UserAccessTokenKey, userAccountId = it.UserAccountId, userAccountName = it.UserAccountName)
            }
          }
          ChannelAccessToken.AccessTokenType.facebookshop.name -> {
            if (it.isValidTypeShop()) {
              data = ChannelAccessToken(type = it.type(), userAccessTokenKey = it.UserAccessTokenKey,
                  userAccountId = it.UserAccountId, userAccountName = it.UserAccountName, pixelId = it.PixelId, catalogId = it.CatalogId, merchantSettingsId = it.MerchantSettingsId)
            }
          }
          ChannelAccessToken.AccessTokenType.googlemybusiness.name.toLowerCase(Locale.ROOT) -> {
            val tokenResponse = ChannelTokenResponse(it.token_response?.access_token, it.token_response?.token_type, it.token_response?.expires_in, it.token_response?.refresh_token)
            data = ChannelAccessToken(type = it.type(), token_expiry = it.token_expiry, invalid = it.invalid, token_response = tokenResponse, refresh_token = it.refresh_token, userAccountName = it.account_name,
                userAccountId = it.account_id, LocationId = it.location_id, LocationName = it.location_name, userAccessTokenKey = it.token_response?.access_token, verified_location = it.verified_location)
          }
        }
        categoryData.channels?.forEach { it1 -> if (it1.getAccessTokenType() == it.type()) it1.channelAccessToken = data }
      }
    }
    getWhatsAppData(categoryData)
  }

  private fun getWhatsAppData(categoryData: CategoryDataModel) {
    viewModel?.getWhatsappBusiness(session?.fpTag, WA_KEY)?.observeOnce(this, {
      if (it.isSuccess()) {
        val response = ((it as? ChannelWhatsappResponse)?.Data)?.firstOrNull()
        if (response != null && response.active_whatsapp_number.isNullOrEmpty().not()) {
          categoryData.channels?.forEach { it1 -> if (it1.isWhatsAppChannel()) it1.channelActionData = ChannelActionData(response.active_whatsapp_number?.trim()) }
        }
      }
      categoryData.saveData()
      setViewChannels(categoryData.channels)
      hideProgress()
    })
  }

  private fun showErrorChannel(message: String) {
    showShortToast(message)
    hideProgress()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.READING_SCORE_CLICK.ordinal -> {
        WebEngageController.trackEvent("SITE HEALTH Page", "SITE_HEALTH", session?.fpTag);
        startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, 0) })
      }
      RecyclerViewActionType.BUSINESS_SETUP_SCORE_CLICK.ordinal -> startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, position) })
      RecyclerViewActionType.QUICK_ACTION_ITEM_CLICK.ordinal -> {
        val data = item as? QuickActionItem ?: return
        QuickActionData.QuickActionType.from(data.quickActionType)?.let { quickActionClick(it) }
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
        val data = item as? PromoAcademyBanner ?: return
        session?.let { baseActivity.promoBannerMarketplace(it, data) }
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnNotofication -> session?.let { baseActivity.startNotification(it) }
      binding?.btnVisitingCardUp -> {
        isExpendCard = false
        visitingCardShowHide(true)
      }
      binding?.btnVisitingCardDown -> {
        isExpendCard = true
        visitingCardShowHide(false)
      }
      binding?.btnBusinessLogo -> baseActivity.startBusinessLogo(session)
      binding?.txtDomainName -> baseActivity.startWebViewPageLoad(session, session!!.getDomainName(false))
      binding?.btnDigitalChannel -> session?.let { baseActivity.startDigitalChannel(it) }
      binding?.btnShowDigitalScore -> {
        WebEngageController.trackEvent("SITE HEALTH Page", "SITE_HEALTH", session?.fpTag);
        startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, 0) })
      }
      binding?.btnShareWhatsapp -> shareUserDetail(true)
      binding?.btnShareMore -> shareUserDetail(false)
    }
  }

  private fun shareUserDetail(isWhatsApp: Boolean) {
    viewModel?.getBoostUserDetailMessage(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val response = it as? ShareUserDetailResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val messageDetail = response.data?.firstOrNull { it1 -> it1.type?.toLowerCase(Locale.ROOT) == session?.fP_AppExperienceCode?.toLowerCase(Locale.ROOT) }?.message
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


  private fun quickActionClick(type: QuickActionData.QuickActionType) {
    when (type) {
      QuickActionData.QuickActionType.POST_NEW_UPDATE -> baseActivity.startPostUpdate(session)
      QuickActionData.QuickActionType.ADD_PHOTO_GALLERY -> baseActivity.startAddImageGallery(session)
      QuickActionData.QuickActionType.ADD_TESTIMONIAL -> baseActivity.startAddTestimonial(session, true)
      QuickActionData.QuickActionType.ADD_CUSTOM_PAGE -> baseActivity.startCreateCustomPage(session, true)
      QuickActionData.QuickActionType.LIST_SERVICES,
      QuickActionData.QuickActionType.LIST_PRODUCT,
      QuickActionData.QuickActionType.LIST_DRUG_MEDICINE,
      -> baseActivity.startListServiceProduct(session)
      QuickActionData.QuickActionType.ADD_SERVICE,
      QuickActionData.QuickActionType.ADD_PRODUCT,
      QuickActionData.QuickActionType.ADD_COURSE,
      QuickActionData.QuickActionType.ADD_MENU,
      QuickActionData.QuickActionType.ADD_ROOM_TYPE,
      -> baseActivity.startAddServiceProduct(session)
      QuickActionData.QuickActionType.PLACE_APPOINTMENT -> baseActivity.startBookAppointmentConsult(session, false)
      QuickActionData.QuickActionType.PLACE_CONSULT -> baseActivity.startBookAppointmentConsult(session, true)
      QuickActionData.QuickActionType.ADD_PROJECT -> {
        if (session?.getStoreWidgets()?.equals(PremiumCode.PROJECTTEAM.value) == true) {
          baseActivity.startListProject(session)
        } else baseActivity.startListProjectAndTeams(session)
      }
      QuickActionData.QuickActionType.ADD_TEAM_MEMBER -> {
        if (session?.getStoreWidgets()?.equals(PremiumCode.PROJECTTEAM.value) == true) {
          baseActivity.startListTeams(session)
        } else baseActivity.startListProjectAndTeams(session)
      }
      QuickActionData.QuickActionType.UPLOAD_BROCHURE -> {
        if (session?.getStoreWidgets()?.equals(PremiumCode.BROCHURE.value) == true) {
          baseActivity.startAddDigitalBrochure(session)
        } else baseActivity.startListDigitalBrochure(session)
      }
      QuickActionData.QuickActionType.POST_SEASONAL_OFFER -> baseActivity.startAddSeasonalOffer(session)
      QuickActionData.QuickActionType.LIST_TOPPER -> baseActivity.startListToppers(session)
      QuickActionData.QuickActionType.ADD_UPCOMING_BATCH -> baseActivity.startListBatches(session)
      QuickActionData.QuickActionType.ADD_NEARBY_ATTRACTION -> baseActivity.startNearByView(session)
      QuickActionData.QuickActionType.ADD_FACULTY_MEMBER -> baseActivity.startFacultyMember(session)

      QuickActionData.QuickActionType.POST_STATUS_STORY,
      QuickActionData.QuickActionType.ADD_SLIDER_BANNER,
      QuickActionData.QuickActionType.PLACE_ORDER_BOOKING,
      QuickActionData.QuickActionType.ADD_TABLE_BOOKING,
      QuickActionData.QuickActionType.ADD_STAFF_MEMBER,
      QuickActionData.QuickActionType.MAKE_ANNOUNCEMENT,
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

  private fun bottomSheetWebView(title: String, domainUrl: String) {
    val webViewBottomDialog = WebViewBottomDialog()
    webViewBottomDialog.setData(title, domainUrl)
    webViewBottomDialog.show(this@DashboardFragment.parentFragmentManager, WebViewBottomDialog::class.java.name)
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


private fun LinearLayoutCompat?.animateViewTopPadding(isDown: Boolean, isStart: Boolean = false) {
  this?.apply {
    val animator: ValueAnimator = ValueAnimator.ofInt(paddingTop, resources.getDimensionPixelSize(if (isDown) R.dimen.size_0 else R.dimen.size_164))
    animator.addUpdateListener { valueAnimator -> setPadding(0, (valueAnimator.animatedValue as Int), 0, 0) }
    animator.duration = if (isStart) 0 else 280
    animator.start()
  }
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
