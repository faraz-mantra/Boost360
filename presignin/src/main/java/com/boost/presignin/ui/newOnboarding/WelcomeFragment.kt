package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentWelcomeBinding
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*
import com.onboarding.nowfloats.utils.WebEngageController

class WelcomeFragment : AppBaseFragment<FragmentWelcomeBinding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): WelcomeFragment {
      val fragment = WelcomeFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  private val whatsappConsent by lazy {
    arguments?.getBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name)
  }

  private val phoneNumber by lazy {
    arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }

  override fun getLayout(): Int {
    return R.layout.fragment_welcome
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(
      SETUP_MY_WEBSITE_SCREEN_LOAD,
      SETUP_MY_WEBSITE_LOAD,
      NO_EVENT_VALUE
    )
    setOnClickListeners()
  }

  private fun setOnClickListeners() {
    binding?.btnSetUpMyWebsite?.setOnClickListener {
      WebEngageController.trackEvent(
        SETUP_MY_WEBSITE_BUTTON_CLICK,
        SETUP_MY_WEBSITE_BTN_CLICK,
        CLICKED
      )
      startFragmentFromNewOnBoardingActivity(
        activity = baseActivity,
        type = FragmentType.SET_UP_MY_WEBSITE_FRAGMENT,
        bundle = Bundle().apply {
          putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
          putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent?:false)
        }
      )
    }
  }
}