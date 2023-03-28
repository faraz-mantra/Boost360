package com.boost.presignin.ui.newOnboarding

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentSetupMyWebsiteBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.onBoardingInfo.OnBoardingInfo
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.ui.login.LoginActivity
import com.boost.presignin.ui.newOnboarding.bottomSheet.NeedHelpBottomSheet
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.framework.pref.APPLICATION_JIO_ID
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId2
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PS_LOGIN_OTP_NEED_HELP_CLICK

class SetupMyWebsiteFragment : AppBaseFragment<FragmentSetupMyWebsiteBinding, LoginSignUpViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SetupMyWebsiteFragment {
      val fragment = SetupMyWebsiteFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  private val TAG = "SetupMyWebsiteFragment"
  private var session: UserSessionManager? = null
  private var responseCreateProfile: BusinessProfileResponse? = null
  var categoryFloatsReq: CategoryFloatsRequest? = null
  var createProfileReq: CreateProfileRequest? = null
  var callIconItem: MenuItem? = null
  var doNewFlowEnabled:Boolean? = null

  private val phoneNumber by lazy {
    arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }

  private val whatsappConsent by lazy {
    arguments?.getBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name)
  }

  override fun getLayout(): Int {
    return R.layout.fragment_setup_my_website
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    this.session = UserSessionManager(baseActivity)
    doNewFlowEnabled = FirebaseRemoteConfigUtil.doNewOnBoardingJourneyEnabled()
    if (doNewFlowEnabled!!){
      showProgress()
      viewModel?.getNewOnBoardingData(phoneNumber!!, clientId2)
        ?.observeOnce(viewLifecycleOwner) {
          if (it.isSuccess()) {
            hideProgress()
            val onBoardingDataResponse = it as? OnBoardingInfo
            if (onBoardingDataResponse?.data?.screen == null) {
              addFragment(
                R.id.inner_container,
                SetupMyWebsiteStep2Fragment.newInstance(Bundle().apply {
                  putString(IntentConstant.DESKTOP_PREVIEW.name, null)
                  putString(IntentConstant.MOBILE_PREVIEW.name, null)
                  putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
                  putString(IntentConstant.CATEGORY_SUGG_UI.name, null)
                  putString(IntentConstant.SUB_CATEGORY_ID.name, null)
                  putSerializable(IntentConstant.CATEGORY_DATA.name, null)
                  putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent!!)
                }),
                true
              )
            }  else if (onBoardingDataResponse.data.screen == "One") {
              addFragment(
                R.id.inner_container,
                SetupMyWebsiteStep2Fragment.newInstance(Bundle().apply {
                  putString(IntentConstant.DESKTOP_PREVIEW.name, null)
                  putString(IntentConstant.MOBILE_PREVIEW.name, null)
                  putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
                  putString(IntentConstant.CATEGORY_SUGG_UI.name, null)
                  putString(IntentConstant.SUB_CATEGORY_ID.name, null)
                  putSerializable(IntentConstant.CATEGORY_DATA.name, null)
                  putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent!!)
                }),
                true
              )
            }else if (onBoardingDataResponse.data.screen == "Two") {
              addFragment(
                R.id.inner_container, SetupMyWebsiteStep3Fragment.newInstance(
                  Bundle().apply {
                    putString(IntentConstant.DESKTOP_PREVIEW.name, null)
                    putString(IntentConstant.MOBILE_PREVIEW.name, null)
                    putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
                    putString(IntentConstant.CATEGORY_SUGG_UI.name, null)
                    putString(IntentConstant.SUB_CATEGORY_ID.name, null)
                    putSerializable(IntentConstant.CATEGORY_DATA.name, null)
                    putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent ?: false)
                    putString(IntentConstant.EXTRA_BUSINESS_NAME.name,onBoardingDataResponse.data.businessName)
                  }), true
              )
            }else if (onBoardingDataResponse.data.screen == "Three"){
              addFragment(
                R.id.inner_container, SetupMyWebsiteStep1Fragment.newInstance(
                  Bundle().apply {
                    putString(IntentConstant.DESKTOP_PREVIEW.name, null)
                    putString(IntentConstant.MOBILE_PREVIEW.name, null)
                    putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
                    putString(IntentConstant.CATEGORY_SUGG_UI.name, null)
                    putString(IntentConstant.SUB_CATEGORY_ID.name, null)
                    putSerializable(IntentConstant.CATEGORY_DATA.name, null)
                    putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent ?: false)
                    putString(IntentConstant.EXTRA_BUSINESS_NAME.name, onBoardingDataResponse.data.businessName)
                    putString(IntentConstant.BUSINESS_DOMAIN.name,onBoardingDataResponse.data.domainName)
                  }), true
              )
            }else if (onBoardingDataResponse.data.screen == "Four"){
              addFragment(R.id.inner_container, BusinessCategoryPreviewFragment.newInstance(Bundle().apply {
                putString(IntentConstant.DESKTOP_PREVIEW.name, onBoardingDataResponse.data.desktopPreview)
                putString(IntentConstant.MOBILE_PREVIEW.name, onBoardingDataResponse.data.mobilePreview)
                putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
                putString(IntentConstant.CATEGORY_SUGG_UI.name, onBoardingDataResponse.data.categoryLiveName)
                putString(IntentConstant.SUB_CATEGORY_ID.name, onBoardingDataResponse.data.subCategoryID)
                putSerializable(IntentConstant.CATEGORY_DATA.name, onBoardingDataResponse.data.selectedCategory)
                putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent ?: false)
                putString(IntentConstant.EXTRA_BUSINESS_NAME.name, onBoardingDataResponse.data.businessName)
                putString(IntentConstant.BUSINESS_DOMAIN.name,onBoardingDataResponse.data.domainName)
              }), true)
            }
          } else {
            hideProgress()
            addFragment(R.id.inner_container, SetupMyWebsiteStep2Fragment.newInstance(Bundle().apply {
              putString(IntentConstant.DESKTOP_PREVIEW.name, null)
              putString(IntentConstant.MOBILE_PREVIEW.name, null)
              putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
              putString(IntentConstant.CATEGORY_SUGG_UI.name, null)
              putString(IntentConstant.SUB_CATEGORY_ID.name, null)
              putSerializable(IntentConstant.CATEGORY_DATA.name, null)
              putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent!!)
            }), true)
          }
        }

    }
    else{
      addFragment(R.id.inner_container, SetupMyWebsiteStep1Fragment.newInstance(Bundle().apply {
        putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
        putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent == true)
      }), true)
    }
    parentFragmentManager.addOnBackStackChangedListener {
      if (getTopFragment() != null) {
        setUpStepUI(getTopFragment()!!)
      } else {
        baseActivity.finish()
      }
    }
  }

  private fun setUpStepUI(fragment: Fragment) {
    if (doNewFlowEnabled!!){
      when (fragment) {
        is SetupMyWebsiteStep2Fragment -> {
          binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
          binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
          binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
          binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
          binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
        }
        is SetupMyWebsiteStep3Fragment -> {
          binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
          binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_7ED321)
          binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
          binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
          binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
        }
        is SetupMyWebsiteStep1Fragment -> {
          binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
          binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_7ED321)
          binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
          binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.green_7ED321)
          binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
        }
      }
    }else{
      when (fragment) {
        is SetupMyWebsiteStep1Fragment -> {
          binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
          binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
          binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
          binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
          binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
        }
        is SetupMyWebsiteStep2Fragment -> {
          binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
          binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_7ED321)
          binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
          binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
          binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
        }
        is SetupMyWebsiteStep3Fragment -> {
          binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
          binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_7ED321)
          binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
          binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.green_7ED321)
          binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
        }
      }
    }
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_help_setup_my_website, menu)
    val menuItem = menu.findItem(R.id.help_new)
    val callIconItem = menuItem?.actionView?.findViewById(R.id.helpCall) as ImageView
    callIconItem.setPadding(0,0,0,0)
    menuItem.actionView?.setOnClickListener {
      menu.performIdentifierAction(menuItem.itemId, 0)
    }
    val menuItemMore = menu.findItem(R.id.more_options)
    menuItemMore.isVisible = true
    val menuItemAnotherLogin = menu.findItem(R.id.action_another_login)
    menuItemAnotherLogin.actionView?.setOnClickListener {
      menu.performIdentifierAction(menuItem.itemId, 0)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.help_new -> {
        WebEngageController.trackEvent(PS_LOGIN_OTP_NEED_HELP_CLICK, CLICK, NO_EVENT_VALUE)
        NeedHelpBottomSheet().show(parentFragmentManager, NeedHelpBottomSheet::class.java.name)
        return true
      }
      R.id.action_another_login -> {
        if (baseActivity.packageName.equals(APPLICATION_JIO_ID, ignoreCase = true).not()) {
          startFragmentFromNewOnBoardingActivity(
            activity = baseActivity, type = FragmentType.ENTER_PHONE_FRAGMENT,
            bundle = Bundle().apply { putBoolean("deleteFragment", true) }, clearTop = true
          )
        } else {
          startActivity(Intent(baseActivity, LoginActivity::class.java))
        }
        return true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}