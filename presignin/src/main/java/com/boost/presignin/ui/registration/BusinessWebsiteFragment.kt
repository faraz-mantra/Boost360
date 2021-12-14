package com.boost.presignin.ui.registration

import BusinessDomainRequest
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.Log
import android.widget.ImageView
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentBusinessWebsiteBinding
import com.boost.presignin.dialog.FullScreenProgressDialog
import com.boost.presignin.extensions.isWebsiteValid
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.authToken.saveAuthTokenData
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.saveCategoryRequest
import com.boost.presignin.model.signup.FloatingPointCreateResponse
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.BaseResponse
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.afterTextChanged
import com.framework.extensions.observeOnce
import com.framework.pref.*
import com.framework.webengageconstant.*
import com.invitereferrals.invitereferrals.InviteReferralsApi
import java.util.*

open class BusinessWebsiteFragment : AppBaseFragment<FragmentBusinessWebsiteBinding, LoginSignUpViewModel>() {

  private lateinit var session: UserSessionManager
  private lateinit var fullScreenProgress: FullScreenProgressDialog
  private var floatsRequest: CategoryFloatsRequest? = null
  private var isDomain: Boolean = false
  private var domainValue: String? = null
  private var authToken: AuthTokenDataItem? = null
  private var isSyncCreateFpApi = false
  private var responseCreateProfile: BusinessProfileResponse? = null

  companion object {
    const val CATEGORY_DATA = "category_data"

    @JvmStatic
    fun newInstance(registerRequest: CategoryFloatsRequest) = BusinessWebsiteFragment().apply {
      arguments = Bundle().apply {
        putSerializable(CATEGORY_DATA, registerRequest)
      }
    }
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(PS_BUSINESS_WEBSITE_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    fullScreenProgress = FullScreenProgressDialog.newInstance()
    this.session = UserSessionManager(baseActivity)
    floatsRequest = arguments?.getSerializable(CATEGORY_DATA) as? CategoryFloatsRequest
    val websiteHint = floatsRequest?.businessName?.trim()?.replace(" ", "")
    val websiteHintSpannable = SpannableString("'$websiteHint' ").apply {
      setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
    }

    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }

    binding?.websiteEt?.afterTextChanged {
      val website = binding?.websiteEt?.text.toString()
      if (website.isEmpty().not() && website.length < 3) {
        errorSet()
        return@afterTextChanged
      }
      setSubDomain(website)
      val sp = SpannableString("'$website' ").apply {
        setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
      }
      binding?.websiteStatusTv?.text = SpannableStringBuilder().apply {
        append(sp)
        append(getString(R.string.website_available_text))
      }
    }

    binding?.websiteEt?.setText(websiteHint?.toLowerCase(Locale.ROOT))

    binding?.websiteStatusTv?.text = SpannableStringBuilder().apply {
      append(websiteHintSpannable)
      append(getString(R.string.website_available_text))
    }
    binding?.confirmButton?.setOnClickListener {
      it.isEnabled = false
      val website = binding?.websiteEt?.text?.toString()
      if (!website.isWebsiteValid()) {
        showShortToast(getString(R.string.enter_a_valid_website_name))
        return@setOnClickListener
      }
      WebPreSignInBottomDialog().apply {
        setData(baseActivity.getString(R.string.get_boost_360_tnc))
        onClicked = {
          floatsRequest?.webSiteUrl = "$website${getString(R.string.nowfloats_dot_com)}"
          WebEngageController.trackEvent(PS_BUSINESS_WEBSITE_CLICK, CLICK, NO_EVENT_VALUE)
          apiHitCreateMerchantProfile()
        }
        show(
          this@BusinessWebsiteFragment.parentFragmentManager,
          WebPreSignInBottomDialog::class.java.name
        )
      }
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_business_website
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  private fun setSubDomain(storeName: String, isInitial: Boolean = false) {
    val subDomain = storeName.filter { it.isLetterOrDigit() }
    val lengthDifference = storeName.length - subDomain.length
    if (subDomain != storeName || isInitial) {
      val selection = binding?.websiteEt?.selectionEnd?.minus(lengthDifference) ?: return
      binding?.websiteEt?.setText(subDomain.toLowerCase(Locale.ROOT))
      if (selection > 1) binding?.websiteEt?.setSelection(selection)
    }
    apiCheckDomain(storeName.toLowerCase(Locale.ROOT))
  }

  private fun errorSet() {
    isDomain = false
    domainValue = ""
    binding?.confirmButton?.isEnabled = false
    binding?.fragmentStatusIv?.isClickable = true
    binding?.websiteStatusTv?.setTextColor(getColor(R.color.red_error_e3954))
    binding?.websiteStatusTv?.text = "'${binding?.websiteEt?.text?.toString()?.trim()}' ${getString(R.string.user_not_available)}"
    binding?.fragmentStatusIv?.setImageResource(R.drawable.ic_error_business_website)
    binding?.fragmentStatusIv?.setOnClickListener { binding?.websiteEt?.text?.clear() }
  }

  private fun apiCheckDomain(subDomain: String, onSuccess: () -> Unit = {}) {
    if (!TextUtils.isEmpty(subDomain)) {
      val data = BusinessDomainRequest(clientId2, subDomain, subDomain)
      viewModel?.postCheckBusinessDomain(data)?.observeOnce(viewLifecycleOwner, {
        onPostBusinessDomainCheckResponse(it, onSuccess)
      })
    } else errorSet()
  }

  private fun onPostBusinessDomainCheckResponse(response: BaseResponse, onSuccess: () -> Unit) {
    if (response.isSuccess() && response.stringResponse.isNullOrEmpty().not()) {
      isDomain = true
      binding?.confirmButton?.isEnabled = true
      domainValue = response.stringResponse?.toLowerCase(Locale.ROOT)
      floatsRequest?.domainName = domainValue
      binding?.fragmentStatusIv?.isClickable = false
      binding?.fragmentStatusIv?.setImageResource(R.drawable.ic_valid)
      binding?.websiteStatusTv?.setTextColor(getColor(R.color.psn_sub_heading_color))
      onSuccess()
    } else errorSet()
  }

  private fun goBack() {
    parentFragmentManager.popBackStackImmediate()
  }

  private fun apiHitCreateMerchantProfile() {
    showProgress("We're creating your online ${floatsRequest?.categoryDataModel?.category_Name}...")
    if (this.responseCreateProfile == null) {
      binding?.confirmButton?.isEnabled = true
      viewModel?.createMerchantProfile(request = floatsRequest?.requestProfile)?.observeOnce(viewLifecycleOwner, {
        val businessProfileResponse = it as? BusinessProfileResponse
        if (it.isSuccess() && businessProfileResponse != null && businessProfileResponse.result?.loginId.isNullOrEmpty().not()) {
          apiHitBusiness(businessProfileResponse)
        } else {
          hideProgress()
          val msg = it?.errorNMessage()
          showShortToast(if (msg.isNullOrEmpty().not()) msg else getString(R.string.unable_to_create_profile))
        }
      })
    } else apiHitBusiness(this.responseCreateProfile!!)
  }


  private fun setReferralCode(floatingPointId: String?) {
    InviteReferralsApi.getInstance(baseActivity).ir_TrackingCallbackListener { tracking_response ->
      Log.e("Tracking", "Response ir_TrackingCallbackListener = $tracking_response")
    }
    InviteReferralsApi.getInstance(baseActivity).userDetailListener { ApiResponse ->
      Log.e("Users", "Response userDetailListener = $ApiResponse")
    }

    val email = if (floatsRequest?.userBusinessEmail.isNullOrEmpty()) "noemail-${floatsRequest?.userBusinessMobile}@noemail.com" else floatsRequest?.userBusinessEmail
    Log.e("Set Referral", "Email: $email")
    InviteReferralsApi.getInstance(baseActivity).userDetails(
      floatsRequest?.requestProfile?.ProfileProperties?.userName,
      email, floatsRequest?.userBusinessMobile, 0, null, null
    )
    InviteReferralsApi.getInstance(baseActivity).tracking("register", email, 0, null, null)
  }

  private fun apiHitBusiness(businessProfileResponse: BusinessProfileResponse) {
    putCreateBusinessOnBoarding(businessProfileResponse)
  }

  private fun putCreateBusinessOnBoarding(response: BusinessProfileResponse) {
    this.responseCreateProfile = response
    val request = getBusinessRequest()
    isSyncCreateFpApi = true
    viewModel?.putCreateBusinessV6(response.result?.loginId, request)?.observeOnce(viewLifecycleOwner, {
      val result = it as? FloatingPointCreateResponse
      if (result?.isSuccess() == true && result.authTokens.isNullOrEmpty().not()) {
        authToken = result.authTokens?.firstOrNull()
        WebEngageController.initiateUserLogin(response.result?.loginId)
        WebEngageController.setUserContactAttributes(
          response.result?.profileProperties?.userEmail, response.result?.profileProperties?.userMobile,
          response.result?.profileProperties?.userName, response.result?.sourceClientId
        )
        WebEngageController.setFPTag(authToken?.floatingPointTag)
        setReferralCode(authToken?.floatingPointId)
        WebEngageController.trackEvent(PS_SIGNUP_SUCCESS, SIGNUP_SUCCESS, NO_EVENT_VALUE)
        session.userProfileId = response.result?.loginId
        session.userProfileEmail = response.result?.profileProperties?.userEmail
        session.userProfileName = response.result?.profileProperties?.userName
        session.userProfileMobile = response.result?.profileProperties?.userMobile
        session.storeISEnterprise(response.result?.isEnterprise.toString() + "")
        session.storeIsThinksity((response.result?.sourceClientId != null && response.result?.sourceClientId == clientIdThinksity).toString() + "")
        session.storeFPDetails(Key_Preferences.GET_FP_EXPERIENCE_CODE, floatsRequest?.categoryDataModel?.experience_code)
        session.storeFPID(authToken?.floatingPointId)
        session.storeFpTag(authToken?.floatingPointTag)
        floatsRequest?.floatingPointId = authToken?.floatingPointId!!
        floatsRequest?.fpTag = authToken?.floatingPointTag
        floatsRequest?.requestProfile?.profileId = response.result?.loginId
        session.saveCategoryRequest(floatsRequest!!)
        session.saveAuthTokenData(authToken!!)
        session.setUserSignUpComplete(true)
        navigator?.clearBackStackAndStartNextActivity(RegistrationActivity::class.java, Bundle().apply { putInt(FRAGMENT_TYPE, SUCCESS_FRAGMENT) })
      } else {
        val msg = it.message()
        showShortToast(if (msg.isNotEmpty()) msg else getString(R.string.error_create_business_fp))
      }
      hideProgress()
    })
  }

  private fun getBusinessRequest(): BusinessCreateRequest {
    val createRequest = BusinessCreateRequest()
    createRequest.autoFillSampleWebsiteData = true
    createRequest.webTemplateId = floatsRequest?.categoryDataModel?.webTemplateId
    createRequest.clientId = clientId
    createRequest.tag = floatsRequest?.domainName
    createRequest.name = floatsRequest?.businessName
    createRequest.address = ""
    createRequest.pincode = ""
    createRequest.country = "India"
    createRequest.primaryNumber = floatsRequest?.userBusinessMobile
    createRequest.email = floatsRequest?.userBusinessEmail
    createRequest.primaryNumberCountryCode = "+91"
    createRequest.uri = ""
    createRequest.primaryCategory = floatsRequest?.categoryDataModel?.category_key
    createRequest.appExperienceCode = floatsRequest?.categoryDataModel?.experience_code
    createRequest.whatsAppNumber = floatsRequest?.userBusinessMobile
    createRequest.whatsAppNotificationOptIn = floatsRequest?.whatsAppFlag ?: false
    createRequest.boostXWebsiteUrl = "www.${floatsRequest?.domainName?.toLowerCase(Locale.ROOT)}.nowfloats.com"
    return createRequest
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    title?.let { fullScreenProgress.setTitle(it) }
    cancelable?.let { fullScreenProgress.isCancelable = it }
    activity?.let { fullScreenProgress.showProgress(it.supportFragmentManager) }
  }

  override fun hideProgress() {
    fullScreenProgress.hideProgress()
  }
}