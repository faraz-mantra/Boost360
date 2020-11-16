package com.dashboard.controller.ui.dashboard

import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.appservice.model.onboardingUpdate.OnBoardingUpdateModel
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.FragmentType
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.startFragmentDashboardActivity
import com.dashboard.databinding.FragmentDashboardBinding
import com.dashboard.model.*
import com.dashboard.pref.BASE_IMAGE_URL
import com.dashboard.pref.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME
import com.dashboard.pref.Key_Preferences.GET_FP_DETAILS_IMAGE_URI
import com.dashboard.pref.Key_Preferences.GET_FP_DETAILS_ROOTALIASURI
import com.dashboard.pref.Key_Preferences.GET_FP_DETAILS_TAG
import com.dashboard.pref.UserSessionManager
import com.dashboard.pref.WA_KEY
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.setGifAnim
import com.dashboard.utils.siteMeterCalculation
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.framework.views.dotsindicator.OffsetPageTransformer
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

  private val isHigh = false

  private var adapterPager1: AppBaseRecyclerViewAdapter<BusinessSetupData>? = null
  private var channelAdapter: AppBaseRecyclerViewAdapter<ChannelData>? = null

  override fun getLayout(): Int {
    return R.layout.fragment_dashboard
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    setOnClickListener(binding?.btnVisitingCardUp, binding?.btnVisitingCardDown)
    setUserData()
    getCategoryData()
    getSiteMeter()

    if (isHigh) {
      binding?.viewLowDigitalReadiness?.gone()
      binding?.viewLowTaskManageBusiness?.gone()
      binding?.viewHighDigitalReadiness?.visible()
      binding?.pagerBusinessSetupHigh?.apply {
        val adapterPager2 = AppBaseRecyclerViewAdapter(baseActivity, BusinessSetupHighData().getData(), this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterPager2
        binding?.dotIndicatorBusinessHigh?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }
    } else {
      binding?.viewHighDigitalReadiness?.gone()
      binding?.viewLowDigitalReadiness?.visible()
      binding?.viewLowTaskManageBusiness?.visible()
      binding?.pagerBusinessSetupLow?.apply {
        adapterPager1 = AppBaseRecyclerViewAdapter(baseActivity, BusinessSetupData().getData(), this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterPager1
        binding?.dotIndicator?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
          override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding?.motionOne?.loadLayoutDescription(takeIf { position == 0 }?.let { R.xml.fragment_dashboard_scene } ?: 0)
          }
        })
        baseActivity.setGifAnim(binding?.missingDetailsGif!!, R.raw.ic_missing_setup_gif_d, R.drawable.ic_custom_page_d)
        baseActivity.setGifAnim(binding?.arrowLeftGif!!, R.raw.ic_arrow_left_gif_d, R.drawable.ic_arrow_right_14_d)
      }
    }

    (if (isHigh) binding?.highRecommendedTask else binding?.lowRecommendedTask)?.apply {
      pagerQuickAction.apply {
        val adapterPager5 = AppBaseRecyclerViewAdapter(baseActivity, QuickActionData().getData(), this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterPager5
        dotIndicatorAction.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }
    }
    (if (isHigh) binding?.highManageBusiness else binding?.lowManageBusiness)?.apply {
      rvManageBusiness.apply {
        val adapter1 = AppBaseRecyclerViewAdapter(baseActivity, ManageBusinessData().getData(), this@DashboardFragment)
        adapter = adapter1
      }
      btnShowAll.setOnClickListener { startFragmentDashboardActivity(FragmentType.ALL_BOOST_ADD_ONS) }
    }

    binding?.pagerRiaAcademy?.apply {
      val adapterPager3 = AppBaseRecyclerViewAdapter(baseActivity, RiaAcademyData().getData(), this@DashboardFragment)
      offscreenPageLimit = 3
      clipToPadding = false
      setPadding(37, 0, 37, 0)
      adapter = adapterPager3
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }

    binding?.pagerBoostPremium?.apply {
      val adapterPager4 = AppBaseRecyclerViewAdapter(baseActivity, BoostPremiumData().getDataChannel(), this@DashboardFragment)
      offscreenPageLimit = 3
      adapter = adapterPager4
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }

    binding?.rvRoiSummary?.apply {
      val adapter2 = AppBaseRecyclerViewAdapter(baseActivity, RoiSummaryData().getData(), this@DashboardFragment)
      adapter = adapter2
    }

    binding?.rvGrowthState?.apply {
      val adapter3 = AppBaseRecyclerViewAdapter(baseActivity, GrowthStatsData().getData(), this@DashboardFragment)
      adapter = adapter3
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnVisitingCardUp -> visitingCardShowHide(true)
      binding?.btnVisitingCardDown -> visitingCardShowHide(false)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.READING_SCORE_CLICK.ordinal -> startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE)
    }
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

  private fun getSiteMeter() {
    val siteMeterTotalWeight = session?.siteMeterCalculation() ?: 0
    if (session?.siteHealth != siteMeterTotalWeight) {
      session?.siteHealth = siteMeterTotalWeight
      val data = OnBoardingUpdateModel()
      data.setData(session?.fPID!!, String.format("site_health:%s", siteMeterTotalWeight))
      viewModel?.fpOnboardingUpdate(WA_KEY, data)?.observeOnce(viewLifecycleOwner, {
        Log.i("DASHBOARD", it.message())
      })
    }
    binding?.txtReadinessScore?.text = "$siteMeterTotalWeight"
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      binding?.progressScore?.setProgress(siteMeterTotalWeight, true)
    } else binding?.progressScore?.progress = siteMeterTotalWeight
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
    binding?.txtDomainName?.text = fromHtml("<u>${getDomainName(true)}</u>")
    var imageUri = session?.getFPDetails(GET_FP_DETAILS_IMAGE_URI)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) {
      imageUri = BASE_IMAGE_URL + imageUri
    }
    binding?.imgBusinessLogo?.let { baseActivity.glideLoad(it, imageUri, R.drawable.ic_add_logo_d, isCrop = true) }
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

  private fun getDomainName(isRemoveHttp: Boolean = false): String? {
    val rootAliasUri = session?.getFPDetails(GET_FP_DETAILS_ROOTALIASURI)?.toLowerCase(Locale.ROOT)
    val normalUri = "${session?.getFPDetails(GET_FP_DETAILS_TAG)?.toLowerCase(Locale.ROOT)}.nowfloats.com"
    return if (rootAliasUri.isNullOrEmpty().not() && rootAliasUri != "null") {
      return if (isRemoveHttp && rootAliasUri!!.contains("http://")) rootAliasUri.replace("http://", "")
      else if (isRemoveHttp && rootAliasUri!!.contains("https://")) rootAliasUri.replace("https://", "") else rootAliasUri
    } else normalUri
  }

  private fun showErrorChannel(message: String) {
    showShortToast(message)
    hideProgress()
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
