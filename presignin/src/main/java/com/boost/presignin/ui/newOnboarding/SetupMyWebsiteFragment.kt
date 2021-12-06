package com.boost.presignin.ui.newOnboarding

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentSetupMyWebsiteBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.authToken.saveAuthTokenData
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.onboardingRequest.saveCategoryRequest
import com.boost.presignin.model.signup.FloatingPointCreateResponse
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.ui.newOnboarding.bottomSheet.HelpSupportSuccessBottomSheet
import com.boost.presignin.ui.newOnboarding.bottomSheet.NeedHelpBottomSheet
import com.boost.presignin.ui.registration.RegistrationActivity
import com.boost.presignin.ui.registration.SUCCESS_FRAGMENT
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientIdThinksity
import com.framework.views.blur.BlurView
import com.framework.views.blur.RenderScriptBlur
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PS_SIGNUP_SUCCESS
import com.framework.webengageconstant.SIGNUP_SUCCESS
import com.invitereferrals.invitereferrals.InviteReferralsApi
import com.onboarding.nowfloats.viewmodel.category.CategoryViewModel
import java.util.*

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
    addFragment(
      R.id.inner_container, SetupMyWebsiteStep1Fragment.newInstance(Bundle()
        .apply
        {
          putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
          putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent == true)

        }), true
    )

    parentFragmentManager.addOnBackStackChangedListener {
      Log.i(TAG, "onCreateView: ")
      if (getTopFragment() != null) {
        setUpStepUI(getTopFragment()!!)
      } else {
        requireActivity().finish()
      }
    }
    //initUI()
  }

  private fun setUpStepUI(fragment: Fragment) {
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
        binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
        binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
        binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
        binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
      }
      is SetupMyWebsiteStep3Fragment -> {
        binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
        binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
        binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
        binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
        binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
      }
    }
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_help_on_boarding_new, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_item_help_onboard -> {
        NeedHelpBottomSheet().show(
          parentFragmentManager,
          NeedHelpBottomSheet::class.java.name
        )
        return true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }


}