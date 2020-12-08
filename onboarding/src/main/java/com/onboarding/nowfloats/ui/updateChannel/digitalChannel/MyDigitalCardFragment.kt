package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import android.os.Bundle
import com.framework.extensions.observeOnce
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.databinding.FragmentDigitalCardBinding
import com.onboarding.nowfloats.extensions.capitalizeWords
import com.onboarding.nowfloats.model.digitalCard.DigitalCardData
import com.onboarding.nowfloats.model.profile.MerchantProfileResponse
import com.onboarding.nowfloats.model.profile.ProfileProperties
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.viewmodel.channel.ChannelPlanViewModel

class MyDigitalCardFragment : AppBaseFragment<FragmentDigitalCardBinding, ChannelPlanViewModel>(), RecyclerItemClickListener {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): MyDigitalCardFragment {
      val fragment = MyDigitalCardFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_digital_card
  }

  override fun onCreateView() {
    super.onCreateView()
    val floatingPoint = arguments?.getString(PreferenceConstant.KEY_FP_ID)
    val businessName = arguments?.getString(PreferenceConstant.BUSINESS_NAME)?.capitalizeWords()
    val businessImage = arguments?.getString(PreferenceConstant.BUSINESS_IMAGE)
    val location = arguments?.getString(PreferenceConstant.LOCATION)
    val websiteUrl = arguments?.getString(PreferenceConstant.WEBSITE_URL)
    val businessType = arguments?.getString(PreferenceConstant.BUSINESS_TYPE)
    //Optional
    val primaryNumber = arguments?.getString(PreferenceConstant.PRIMARY_NUMBER)
    val primaryEmail = arguments?.getString(PreferenceConstant.PRIMARY_EMAIL)
    val fpTag = arguments?.getString(PreferenceConstant.GET_FP_DETAILS_TAG)

    viewModel?.getMerchantProfile(floatingPoint)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        val response = it as? MerchantProfileResponse
        val userProfile: ProfileProperties?
        userProfile = if (response?.result?.channelProfileProperties.isNullOrEmpty().not()) response?.result?.channelProfileProperties!![0].profileProperties
        else ProfileProperties(userName = fpTag, userMobile = primaryNumber, userEmail = primaryEmail)

        val cardList = ArrayList<DigitalCardData>()
        cardList.add(DigitalCardData(businessName, businessImage, location, (userProfile?.userName ?: fpTag)?.capitalizeWords(),
            userProfile?.userMobile ?: primaryNumber, userProfile?.userEmail ?: primaryEmail,
            businessType, websiteUrl, R.color.darkslategray, R.color.goldenrod, R.color.white, R.color.white))
        cardList.add(DigitalCardData(businessName, businessImage, location, (userProfile?.userName ?: fpTag)?.capitalizeWords(),
            userProfile?.userMobile ?: primaryNumber, userProfile?.userEmail ?: primaryEmail,
            businessType, websiteUrl, R.color.linen, R.color.lightskyblue, R.color.textGreyLight, R.color.textGreyDark))
        setAdapterCard(cardList)
      } else showShortToast(it.message())
    })

  }

  private fun setAdapterCard(cardList: java.util.ArrayList<DigitalCardData>) {
    binding?.pagerDigitalCard?.apply {
      val adapterPager3 = AppBaseRecyclerViewAdapter(baseActivity, cardList, this@MyDigitalCardFragment)
      offscreenPageLimit = 3
      isUserInputEnabled = true
      adapter = adapterPager3
      binding?.dotIndicatorCard?.setViewPager2(this)
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }
  }

  override fun getViewModelClass(): Class<ChannelPlanViewModel> {
    return ChannelPlanViewModel::class.java
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }

}