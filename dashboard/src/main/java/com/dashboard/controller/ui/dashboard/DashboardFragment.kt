package com.dashboard.controller.ui.dashboard

import android.animation.ValueAnimator
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
import com.dashboard.model.live.quickAction.QuickActionData
import com.dashboard.model.live.quickAction.QuickActionItem
import com.dashboard.model.live.quickAction.QuickActionResponse
import com.dashboard.model.live.siteMeter.SiteMeterScoreDetails
import com.dashboard.pref.*
import com.dashboard.pref.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME
import com.dashboard.pref.Key_Preferences.GET_FP_DETAILS_IMAGE_URI
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.inventoryorder.model.floatMessage.MessageModel
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.summary.SummaryEntity
import com.inventoryorder.model.summary.UserSummaryResponse
import com.inventoryorder.model.summaryCall.CallSummaryResponse
import com.inventoryorder.rest.response.OrderSummaryResponse
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.ChannelTokenResponse
import com.onboarding.nowfloats.model.channel.getAccessTokenType
import com.onboarding.nowfloats.model.channel.isWhatsAppChannel
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.model.channel.respose.NFXAccessToken
import com.onboarding.nowfloats.rest.response.category.ResponseDataCategory
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import com.onboarding.nowfloats.rest.response.channel.ChannelsAccessTokenResponse
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DashboardFragment : AppBaseFragment<FragmentDashboardBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var isHigh = false
  private var adapterBusinessContent: AppBaseRecyclerViewAdapter<BusinessContentSetupData>? = null
  private var channelAdapter: AppBaseRecyclerViewAdapter<ChannelData>? = null
  private var adapterQuickAction: AppBaseRecyclerViewAdapter<QuickActionData>? = null
  private var adapterBusinessData: AppBaseRecyclerViewAdapter<ManageBusinessData>? = null

  override fun getLayout(): Int {
    return R.layout.fragment_dashboard
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    setOnClickListener(binding?.btnVisitingCardUp, binding?.btnVisitingCardDown, binding?.btnShowDigitalScore, binding?.btnDigitalChannel)
    val versionName: String = baseActivity.packageManager.getPackageInfo(baseActivity.packageName, 0).versionName
    binding?.txtVersion?.text = "Version $versionName"
    getCategoryData()
    apiSellerSummary()
    setDataRiaAcademy()
  }

  override fun onResume() {
    super.onResume()
    if (MessageModel().getMessageFloatData() != null) session?.siteMeterData { it?.let { it1 -> refreshData(it1) } }
    else getFloatMessage()
  }

  private fun getFloatMessage() {
    viewModel?.getBizFloatMessage(getRequestFloat())?.observeOnce(this, {
      if (it?.isSuccess() == true) (it as? MessageModel)?.saveData()
      session?.siteMeterData { it1 -> it1?.let { it2 -> refreshData(it2) } }
    })
  }

  private fun refreshData(siteMeterData: SiteMeterScoreDetails) {
    (baseActivity as? DashboardActivity)?.setPercentageData(siteMeterData.siteMeterTotalWeight)
    isHigh = (siteMeterData.siteMeterTotalWeight >= 80)
    setUserData()
    setRecBusinessManageTask()
    setGrowthStatHigh()
    getNotificationCount()
    getSiteMeter(siteMeterData)
    setDataSellerSummary(OrderSummaryModel().getSellerSummary(), getSummaryDetail(), CallSummaryResponse().getCallSummary())
  }

  private fun visitingCardShowHide(isDown: Boolean) {
    Timer().schedule(if (isDown) 60 else if (isHigh) 200 else 150) {
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
    binding?.viewDigitalScore?.animateViewTopPadding(isDown)
  }

  private fun getSiteMeter(siteMeterData: SiteMeterScoreDetails) {
    val listDigitalScore = siteMeterData.getListDigitalScore()
    if (isHigh) {
      binding?.viewLowDigitalReadiness?.gone()
      binding?.viewLowTaskManageBusiness?.gone()
      binding?.viewHighDigitalReadiness?.visible()
      binding?.pagerBusinessSetupHigh?.apply {
        val adapterPager2 = AppBaseRecyclerViewAdapter(baseActivity, BusinessSetupHighData().getData(siteMeterData.siteMeterTotalWeight), this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterPager2
        binding?.dotIndicatorBusinessHigh?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }
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

  private fun setGrowthStatHigh() {
    if (isHigh) {
      binding?.viewHighSummaryBottom?.apply { visible() }
      binding?.pagerBoostPremium?.apply {
        val adapterPager4 = AppBaseRecyclerViewAdapter(baseActivity, BoostPremiumData().getDataChannel(), this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterPager4
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }

      binding?.rvRoiSummary?.apply {
        val adapter2 = AppBaseRecyclerViewAdapter(baseActivity, RoiSummaryData().getData(getRoiSummaryType(session?.fP_AppExperienceCode)), this@DashboardFragment)
        adapter = adapter2
      }

      binding?.rvGrowthState?.apply {
        val adapter3 = AppBaseRecyclerViewAdapter(baseActivity, GrowthStatsData().getData(), this@DashboardFragment)
        adapter = adapter3
      }
    } else {
      binding?.viewHighSummaryBottom?.apply { gone() }
    }
  }

  private fun setRecBusinessManageTask() {
    (if (isHigh) binding?.highRecommendedTask else binding?.lowRecommendedTask)?.apply {
      viewModel?.getQuickActionData(baseActivity)?.observeOnce(viewLifecycleOwner, {
        val response = it as? QuickActionResponse
        val listAction = response?.data?.firstOrNull { it1 -> it1.type?.toUpperCase(Locale.ROOT) == session?.fP_AppExperienceCode?.toUpperCase(Locale.ROOT) }
        if (response?.isSuccess() == true && listAction?.actionItem.isNullOrEmpty().not()) {
          if (adapterQuickAction == null) {
            pagerQuickAction.apply {
              adapterQuickAction = AppBaseRecyclerViewAdapter(baseActivity, listAction?.actionItem!!, this@DashboardFragment)
              offscreenPageLimit = 3
              adapter = adapterQuickAction
              dotIndicatorAction.setViewPager2(this)
              setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
            }
          } else adapterQuickAction?.notify(listAction?.actionItem!!)
        } else showShortToast(baseActivity.getString(R.string.quick_action_data_error))
      })

    }
    (if (isHigh) binding?.highManageBusiness else binding?.lowManageBusiness)?.apply {
      title.text = if (getRoiSummaryType(session?.fP_AppExperienceCode) == "DOC") baseActivity.getString(R.string.manage_your_clinic) else baseActivity.getString(R.string.manage_your_business)
      rvManageBusiness.apply {
        viewModel?.getBoostAddOnsTop(baseActivity)?.observeOnce(viewLifecycleOwner, {
          val response = it as? ManageBusinessDataResponse
          val dataAction = response?.data?.firstOrNull { it1 -> it1.type?.toUpperCase(Locale.ROOT) == getAddonsType(session?.fP_AppExperienceCode?.toUpperCase(Locale.ROOT)) }
          if (dataAction != null && dataAction.actionItem.isNullOrEmpty().not()) {
            if (adapterBusinessData == null) {
              adapterBusinessData = AppBaseRecyclerViewAdapter(baseActivity, dataAction.actionItem!!, this@DashboardFragment)
              adapter = adapterBusinessData
            } else adapterBusinessData?.notify(dataAction.actionItem!!)
          } else showShortToast(baseActivity.getString(R.string.manage_business_not_found))
        })
      }
      btnShowAll.setOnClickListener { startFragmentDashboardActivity(FragmentType.ALL_BOOST_ADD_ONS) }
    }
  }

  private fun apiSellerSummary() {
    viewModel?.getSellerSummary(clientId_ORDER, session?.fpTag)?.observeOnce(viewLifecycleOwner, {
      val response1 = it as? OrderSummaryResponse
      if (response1?.isSuccess() == true && response1.Data != null) response1.Data?.saveData()
      val scope = if (session?.iSEnterprise == "true") "1" else "0"
      viewModel?.getUserSummary(clientId, session?.fPParentId, scope)?.observeOnce(viewLifecycleOwner, { it1 ->
        val response2 = it1 as? UserSummaryResponse
        val identifierType = if (session?.iSEnterprise == "true") "MULTI" else "SINGLE"
        viewModel?.getUserCallSummary(clientId, session?.fPParentId, identifierType)?.observeOnce(viewLifecycleOwner, { it2 ->
          val response3 = it2 as? CallSummaryResponse
          response3?.saveData()
          setDataSellerSummary(response1?.Data, response2?.getSummary(), response3)
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

  private fun setDataSellerSummary(sellerOrder: OrderSummaryModel?, summary: SummaryEntity?, response3: CallSummaryResponse?) {

  }

  private fun getSummaryDetail(): SummaryEntity? {
    return SummaryEntity(session?.enquiryCount?.toIntOrNull() ?: 0, session?.subcribersCount?.toIntOrNull() ?: 0, session?.visitorsCount?.toIntOrNull() ?: 0, session?.visitsCount?.toIntOrNull() ?: 0)
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
    var imageUri = session?.getFPDetails(GET_FP_DETAILS_IMAGE_URI)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) {
      imageUri = BASE_IMAGE_URL + imageUri
    }
    binding?.imgBusinessLogo?.let { baseActivity.glideLoad(it, imageUri, R.drawable.ic_add_logo_d, isCrop = true) }
  }

  private fun setDataRiaAcademy() {
    binding?.pagerRiaAcademy?.apply {
      val adapterPager3 = AppBaseRecyclerViewAdapter(baseActivity, RiaAcademyData().getData(), this@DashboardFragment)
      offscreenPageLimit = 3
      clipToPadding = false
      setPadding(37, 0, 37, 0)
      adapter = adapterPager3
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
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
      RecyclerViewActionType.READING_SCORE_CLICK.ordinal -> startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, 0) })
      RecyclerViewActionType.BUSINESS_SETUP_SCORE_CLICK.ordinal -> startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, position) })
      RecyclerViewActionType.QUICK_ACTION_ITEM_CLICK.ordinal -> {
        val data = item as? QuickActionItem ?: return
        QuickActionData.QuickActionType.from(data.quickActionType)?.let { quickActionClick(it) }
      }
      RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal -> {
        val data = item as? ManageBusinessData ?: return
        ManageBusinessData().saveLastSeenData(data)
        ManageBusinessData.BusinessType.fromName(data.businessType)?.let { businessAddOnsClick(it) }
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnVisitingCardUp -> visitingCardShowHide(true)
      binding?.btnVisitingCardDown -> visitingCardShowHide(false)
      binding?.btnDigitalChannel -> session?.let { baseActivity.startDigitalChannel(it) }
      binding?.btnShowDigitalScore -> startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, 0) })
    }
  }


  private fun quickActionClick(type: QuickActionData.QuickActionType) {
    when (type) {
      QuickActionData.QuickActionType.POST_STATUS_STORY -> {
      }
      QuickActionData.QuickActionType.POST_NEW_UPDATE -> {
      }
      QuickActionData.QuickActionType.PLACE_ORDER_APT_BOOKING -> {
      }
      QuickActionData.QuickActionType.ADD_PHOTO_GALLERY -> {
      }
      QuickActionData.QuickActionType.ADD_TESTIMONIAL -> {
      }
      QuickActionData.QuickActionType.ADD_CUSTOM_PAGE -> {
      }
    }
  }

  private fun businessAddOnsClick(type: ManageBusinessData.BusinessType) {
    when (type) {
      ManageBusinessData.BusinessType.ic_project_terms_d -> {
      }
      ManageBusinessData.BusinessType.ic_digital_brochures_d -> {
      }
      ManageBusinessData.BusinessType.ic_customer_call_d -> {
      }
      ManageBusinessData.BusinessType.ic_customer_enquiries_d -> {
      }
      ManageBusinessData.BusinessType.ic_daily_business_update_d -> {
      }
      ManageBusinessData.BusinessType.ic_product_cataloge_d -> {
      }
      ManageBusinessData.BusinessType.ic_customer_testimonial_d -> {
      }
      ManageBusinessData.BusinessType.ic_business_keyboard_d -> {
      }
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

  private fun getRequestFloat(): Map<String, String> {
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    map["skipBy"] = "0"
    map["fpId"] = session?.fPID!!
    return map
  }
}


private fun LinearLayoutCompat?.animateViewTopPadding(isDown: Boolean) {
  this?.apply {
    val animator: ValueAnimator = ValueAnimator.ofInt(paddingTop, resources.getDimensionPixelSize(if (isDown) R.dimen.size_0 else R.dimen.size_164))
    animator.addUpdateListener { valueAnimator -> setPadding(0, (valueAnimator.animatedValue as Int), 0, 0) }
    animator.duration = 280
    animator.start()
  }
}
