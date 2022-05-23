package com.boost.presignin.ui.newOnboarding

import BusinessDomainRequest
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.appservice.utils.capitalizeUtil
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep3Binding
import com.boost.presignin.extensions.validateLetters
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.authToken.saveAuthTokenData
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.onboardingRequest.saveCategoryRequest
import com.boost.presignin.model.signup.FloatingPointCreateResponse
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.*
import com.framework.utils.showKeyBoard
import com.framework.views.blur.setBlur
import com.framework.webengageconstant.*
import com.invitereferrals.invitereferrals.InviteReferralsApi
import org.json.JSONObject

class SetupMyWebsiteStep3Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep3Binding, LoginSignUpViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep3Fragment {
      val fragment = SetupMyWebsiteStep3Fragment()
      fragment.arguments = bundle
      return fragment
    }
  }

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

  private val categoryLiveName by lazy {
    arguments?.getString(IntentConstant.CATEGORY_SUGG_UI.name)
  }

  private val mobilePreview by lazy {
    arguments?.getString(IntentConstant.MOBILE_PREVIEW.name)
  }

  private val desktopPreview by lazy {
    arguments?.getString(IntentConstant.DESKTOP_PREVIEW.name)
  }

  private val categoryModel by lazy {
    arguments?.getSerializable(IntentConstant.CATEGORY_DATA.name) as? CategoryDataModel
  }

  private val businessName by lazy {
    arguments?.getString(IntentConstant.EXTRA_BUSINESS_NAME.name)
  }

  override fun getLayout(): Int {
    return R.layout.layout_set_up_my_website_step_3
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PS_BUSINESS_WEBSITE_PAGE_LOAD_NEW_UPPERCASE, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.includeMobileView?.blurView?.setBlur(baseActivity, 1F)
    session = UserSessionManager(baseActivity)
    binding?.includeMobileView?.tvCategoryName?.text = categoryModel?.getCategoryWithoutNewLine() ?: ""
    binding?.includeMobileView?.tvTitle?.text = businessName?.capitalizeUtil()
    setOnClickListeners()
    binding?.addressInputLayout?.etInput?.setText(businessName?.replace("\\s+".toRegex(), "")?.lowercase())
    apiCheckDomain { websiteNameFieldUiVisibility(websiteNameFieldVisibility = 1) }
  }

  private fun apiCheckDomain(onSuccess: () -> Unit) {
    showProgress()
    val subDomain = binding?.addressInputLayout?.etInput?.text.toString().lowercase()
    if (!TextUtils.isEmpty(subDomain)) {
      val data = BusinessDomainRequest(clientId2, subDomain, subDomain)
      viewModel?.postCheckBusinessDomain(data)?.observeOnce(viewLifecycleOwner) { response ->
        if (response.isSuccess() && response.stringResponse.isNullOrEmpty().not()) {
          onSuccess.invoke()
        } else websiteNameFieldUiVisibility(websiteNameFieldVisibility = 2)
      }
    } else websiteNameFieldUiVisibility(websiteNameFieldVisibility = 2)
  }

  private fun setOnClickListeners() {
    binding?.tvNextStep3?.setOnClickListener {
      if (binding?.addressInputLayout?.etInput?.text?.trim().toString().validateLetters()) {
        if (binding?.tvNextStep3?.text == getString(R.string.launch_my_website)) {
          WebEngageController.trackEvent(PS_BUSINESS_WEBSITE_CLICK_NEW_UPPERCASE, CLICK, NO_EVENT_VALUE)
        }
        apiCheckDomain { apiHitCreateMerchantProfile() }
      } else showShortToast(getString(R.string.website_name_format_invalid_toast))
    }

    binding?.addressInputLayout?.etInput?.afterTextChanged {
      binding?.tvNextStep3?.isEnabled = it.isEmpty().not()
      binding?.includeMobileView?.tvWebsiteName?.text = it
    }

    binding?.addressInputLayout?.etInput?.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        if (binding?.addressInputLayout?.etInput?.text?.trim().toString().validateLetters()) {
          if (binding?.addressInputLayout?.etInput?.text?.trim()?.isEmpty() == false) {
            binding?.addressInputLayout?.etInput?.isEnabled = false
            binding?.addressInputLayout?.ivIcon?.visible()
            apiCheckDomain { websiteNameFieldUiVisibility(websiteNameFieldVisibility = 1) }
          }
        } else showShortToast(getString(R.string.website_name_format_invalid_toast))
      }
      false
    }

    binding?.addressInputLayout?.etInput?.onFocusChangeListener =
      View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) binding?.addressInputLayout?.inputLayout?.setBackgroundResource(R.drawable.bg_dark_stroke_et_onboard)
      }

    binding?.addressInputLayout?.ivIcon?.setOnClickListener {
//      websiteNameFieldUiVisibility()
//      baseActivity.showKeyBoard(binding?.addressInputLayout?.etInput)
      baseActivity.onNavPressed()
    }
  }

  private fun putCreateBusinessOnBoarding(response: BusinessProfileResponse) {
    this.responseCreateProfile = response
    val request = getBusinessRequest()
    viewModel?.putCreateBusinessV6(response.result?.loginId, request)?.observeOnce(viewLifecycleOwner) {
      val result = it as? FloatingPointCreateResponse
      if (result?.isSuccess() == true && result.authTokens.isNullOrEmpty().not()) {
        val authToken = result.authTokens?.firstOrNull()
        WebEngageController.initiateUserLogin(response.result?.loginId)
        WebEngageController.setUserContactAttributes(
          response.result?.profileProperties?.userEmail, response.result?.profileProperties?.userMobile,
          response.result?.profileProperties?.userName, response.result?.sourceClientId
        )
        WebEngageController.setFPTag(authToken?.floatingPointTag)
        setReferralCode(authToken?.floatingPointId)
        WebEngageController.trackEvent(PS_SIGNUP_SUCCESS, SIGNUP_SUCCESS, NO_EVENT_VALUE)
        session?.userProfileId = response.result?.loginId
        session?.userProfileEmail = response.result?.profileProperties?.userEmail
        session?.userProfileName = response.result?.profileProperties?.userName
        session?.userProfileMobile = response.result?.profileProperties?.userMobile
        session?.storeISEnterprise(response.result?.isEnterprise.toString() + "")
        session?.storeIsThinksity((response.result?.sourceClientId != null && response.result?.sourceClientId == clientIdThinksity).toString() + "")
        session?.storeFPDetails(Key_Preferences.GET_FP_EXPERIENCE_CODE, categoryFloatsReq?.categoryDataModel?.experience_code)
        session?.storeFPID(authToken?.floatingPointId)
        session?.storeFpTag(authToken?.floatingPointTag)
        categoryFloatsReq?.floatingPointId = authToken?.floatingPointId!!
        categoryFloatsReq?.fpTag = authToken.floatingPointTag
        categoryFloatsReq?.requestProfile?.profileId = response.result?.loginId
        session?.saveCategoryRequest(categoryFloatsReq!!)
        session?.saveAuthTokenData(authToken)
        session?.setUserSignUpComplete(true)
        startFragmentFromNewOnBoardingActivity(activity = baseActivity, type = FragmentType.LOADING_ANIMATION_DASHBOARD_FRAGMENT, bundle = arguments ?: Bundle(), clearTop = true)
      } else showShortToast(it.message().ifEmpty { getString(R.string.error_create_business_fp) })
      hideProgress()
    }
  }

  private fun initRequest() {
    val domain = binding?.addressInputLayout?.etInput?.text?.toString() ?: ""
    createProfileReq = CreateProfileRequest(ProfileProperties = BusinessInfoModel(userMobile = phoneNumber))
    createProfileReq?.AuthToken = phoneNumber
    createProfileReq?.ClientId = clientId
    createProfileReq?.LoginKey = phoneNumber
    createProfileReq?.LoginSecret = ""
    createProfileReq?.Provider = "EMAIL"
    categoryFloatsReq = CategoryFloatsRequest(
      requestProfile = createProfileReq, categoryDataModel = categoryModel, webSiteUrl = "$domain${getString(R.string.nowfloats_dot_com)}",
      businessName = businessName, desktopPreview = desktopPreview, mobilePreview = mobilePreview
    )
  }

  private fun getBusinessRequest(): BusinessCreateRequest {
    val domain = binding?.addressInputLayout?.etInput?.text?.toString() ?: ""
    val createRequest = BusinessCreateRequest()
    createRequest.autoFillSampleWebsiteData = true
    createRequest.webTemplateId = categoryModel?.webTemplateId
    createRequest.clientId = clientId
    createRequest.tag = domain
    createRequest.name = businessName
    createRequest.address = ""
    createRequest.pincode = ""
    createRequest.country = "India"
    createRequest.primaryNumber = phoneNumber
    // createRequest.email = floatsRequest?.userBusinessEmail
    createRequest.primaryNumberCountryCode = "+91"
    createRequest.uri = ""
    createRequest.primaryCategory = categoryModel?.category_key
    createRequest.appExperienceCode = categoryModel?.experience_code
    createRequest.whatsAppNumber = if (whatsappConsent == true) phoneNumber else null
    createRequest.whatsAppNotificationOptIn = whatsappConsent
    createRequest.boostXWebsiteUrl = "www.${domain.lowercase()}.nowfloats.com"
    return createRequest
  }

  private fun setReferralCode(floatingPointId: String?) {
    InviteReferralsApi.getInstance(baseActivity).ir_TrackingCallbackListener { tracking_response ->
      Log.e("Tracking", "Response ir_TrackingCallbackListener = $tracking_response")
    }
    InviteReferralsApi.getInstance(baseActivity).userDetailListener { ApiResponse ->
      Log.e("Users", "Response userDetailListener = $ApiResponse")
    }

    val email = if (categoryFloatsReq?.userBusinessEmail.isNullOrEmpty()) "noemail-${categoryFloatsReq?.userBusinessMobile}@noemail.com" else categoryFloatsReq?.userBusinessEmail
    Log.e("Set Referral", "Email: $email")
    InviteReferralsApi.getInstance(baseActivity).userDetails(
      categoryFloatsReq?.requestProfile?.ProfileProperties?.userName,
      email, categoryFloatsReq?.userBusinessMobile, 0, null, null
    )
    InviteReferralsApi.getInstance(baseActivity).tracking("register", email, 0, null, null, null, JSONObject())
  }

  private fun apiHitCreateMerchantProfile() {
    initRequest()
    WebEngageController.trackEvent(PS_SIGNUP_LAUNCHING_TRANSITION, PAGE_VIEW, NO_EVENT_VALUE)
    if (this.responseCreateProfile == null) {
      viewModel?.createMerchantProfile(request = categoryFloatsReq?.requestProfile)?.observeOnce(viewLifecycleOwner) {
        val businessProfileResponse = it as? BusinessProfileResponse
        if (it.isSuccess() && businessProfileResponse != null && businessProfileResponse.result?.loginId.isNullOrEmpty().not()) {
          putCreateBusinessOnBoarding(businessProfileResponse)
        } else {
          hideProgress()
          showShortToast(it?.errorFlowMessage() ?: getString(R.string.unable_to_create_profile))
        }
      }
    } else putCreateBusinessOnBoarding(this.responseCreateProfile!!)
  }


  /**
   * This function enables and disabled error mode in case name is invalid
   * @param websiteNameFieldVisibility : Mode of UI Visibility
   *  0 or else : Normal Edit Mode
   *  1 : Green Valid Name Mode
   *  2 : Red Error Mode
   * */
  private fun websiteNameFieldUiVisibility(websiteNameFieldVisibility: Int = 0) {
    hideProgress()
    when (websiteNameFieldVisibility) {
      1 -> {
        binding?.addressInputLayout?.tvWebsiteExtension?.visible()
        binding?.addressInputLayout?.ivStatus?.visible()
        binding?.addressInputLayout?.ivIcon?.visible()
        binding?.addressInputLayout?.ivStatus?.setImageResource(R.drawable.ic_domain_tick)
        binding?.addressInputLayout?.inputLayout?.setBackgroundResource(R.drawable.bg_green_stroke_et)
        binding?.tvNextStep3?.isEnabled = true
        binding?.tvNameNotAvailableError?.gone()
        binding?.linearSecureWrapper?.visible()
        binding?.tvNextStep3?.text = getString(R.string.launch_my_website)
        binding?.addressInputLayout?.etInput?.apply {
          layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
          isEnabled = false
        }
      }
      2 -> {
        binding?.addressInputLayout?.tvWebsiteExtension?.visible()
        binding?.addressInputLayout?.ivStatus?.visible()
        binding?.addressInputLayout?.ivIcon?.visible()
        binding?.addressInputLayout?.ivStatus?.setImageResource(R.drawable.ic_tick_red_error)
        binding?.addressInputLayout?.inputLayout?.setBackgroundResource(R.drawable.bg_red_stroke_et)
        binding?.tvNextStep3?.isEnabled = false
        binding?.tvNameNotAvailableError?.visible()
        binding?.linearSecureWrapper?.gone()
        binding?.tvNextStep3?.text = getString(R.string.launch_my_website)
        binding?.addressInputLayout?.etInput?.apply {
          layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
          isEnabled = false
        }
      }
      else -> {
        binding?.addressInputLayout?.tvWebsiteExtension?.gone()
        binding?.addressInputLayout?.ivIcon?.gone()
        binding?.addressInputLayout?.ivStatus?.gone()
        binding?.tvNameNotAvailableError?.gone()
        binding?.linearSecureWrapper?.gone()
        binding?.addressInputLayout?.inputLayout?.setBackgroundResource(R.drawable.bg_grey_stroke_et)
        binding?.tvNextStep3?.text = getString(R.string.next)
        binding?.addressInputLayout?.etInput?.apply {
          layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
          isEnabled = true
        }
      }
    }
  }
}