package com.boost.presignin.ui.registration

import BusinessDomainRequest
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.widget.ImageView
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentBusinessWebsiteBinding
import com.boost.presignin.extensions.isWebsiteValid
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.RequestFloatsModel
import com.boost.presignin.model.activatepurchase.ActivatePurchasedOrderRequest
import com.boost.presignin.model.activatepurchase.ConsumptionConstraint
import com.boost.presignin.model.activatepurchase.PurchasedExpiry
import com.boost.presignin.model.activatepurchase.PurchasedWidget
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.BaseResponse
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.afterTextChanged
import com.framework.extensions.observeOnce
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.webengageconstant.*
import java.util.*

open class BusinessWebsiteFragment : AppBaseFragment<FragmentBusinessWebsiteBinding, LoginSignUpViewModel>() {

  private var registerRequest: RequestFloatsModel? = null
  private var isDomain: Boolean = false
  private var domainValue: String? = null
  private var floatingPointId = ""
  private var isSyncCreateFpApi = false
  private var responseCreateProfile: BusinessProfileResponse? = null

  companion object {
    @JvmStatic
    fun newInstance(registerRequest: RequestFloatsModel) = BusinessWebsiteFragment().apply {
      arguments = Bundle().apply {
        putSerializable("request", registerRequest)
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
    binding?.fragmentStatusIv?.setImageResource(R.drawable.ic_error)
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
    if (response.error is NoNetworkException) {
      errorSet()
      return
    }
    if (response.stringResponse.isNullOrEmpty().not()) {
      isDomain = true
      binding?.confirmButton?.isEnabled = true
      domainValue = response.stringResponse?.toLowerCase(Locale.ROOT)
      registerRequest?.ProfileProperties?.domainName = domainValue
      binding?.fragmentStatusIv?.setImageResource(R.drawable.ic_valid)
      onSuccess()
    } else errorSet()
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(BUSINESS_PROFILE_INFO, PAGE_VIEW, NO_EVENT_VALUE)
    registerRequest = arguments?.getSerializable("request") as? RequestFloatsModel
    val websiteHint = registerRequest?.ProfileProperties?.businessName?.trim()?.replace(" ", "")
    val amountSpannableString = SpannableString("'$websiteHint' ").apply {
      setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
    }

    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }

    binding?.websiteEt?.afterTextChanged {
      setSubDomain(binding?.websiteEt?.text.toString())
      val sp = SpannableString(binding?.websiteEt?.text.toString()).apply {
        setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
      }
      binding?.websiteStatusTv?.text = SpannableStringBuilder().apply {
        append(sp)
        append(getString(R.string.website_available_text))
      }
    }

    binding?.websiteEt?.setText(websiteHint)

    binding?.websiteStatusTv?.text = SpannableStringBuilder().apply {
      append(amountSpannableString)
      append(getString(R.string.website_available_text))
    }
    binding?.confirmButton?.setOnClickListener {
      val website = binding?.websiteEt?.text?.toString()
      if (!website.isWebsiteValid()) {
        showShortToast("Enter a valid website name")
        return@setOnClickListener
      }
      registerRequest?.webSiteUrl = "$website.nowfloats.com"
      WebEngageController.trackEvent(CREATE_MY_BUSINESS_WEBSITE, CLICK, NO_EVENT_VALUE)
      apiHitCreateMerchantProfile()
    }
  }

  private fun goBack() {
    parentFragmentManager.popBackStackImmediate()
  }

  private fun apiHitCreateMerchantProfile() {
    showProgress()
    if (this.responseCreateProfile == null) {
      viewModel?.createMerchantProfile(request = registerRequest)?.observeOnce(viewLifecycleOwner, {
        val businessProfileResponse = it as? BusinessProfileResponse
        if (it.isSuccess() && businessProfileResponse != null) {
          apiHitBusiness(businessProfileResponse)
        } else {
          hideProgress()
          showShortToast(getString(R.string.unable_to_create_profile))
        }
      })
    } else apiHitBusiness(this.responseCreateProfile!!)
  }


  private fun apiHitBusiness(businessProfileResponse: BusinessProfileResponse) {
    putCreateBusinessOnBoarding(businessProfileResponse)
  }

  private fun putCreateBusinessOnBoarding(response: BusinessProfileResponse) {
    this.responseCreateProfile = response
    val request = getBusinessRequest()
    isSyncCreateFpApi = true
    viewModel?.putCreateBusinessOnboarding(response.result?.loginId, request)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        if (it.stringResponse.isNullOrEmpty().not()) {
          WebEngageController.initiateUserLogin(response.result?.loginId)
          WebEngageController.setUserContactAttributes(response.result?.profileProperties?.userEmail, response.result?.profileProperties?.userMobile, response.result?.profileProperties?.userName, response.result?.sourceClientId)
          WebEngageController.trackEvent(PS_SIGNUP_SUCCESS, SIGNUP_SUCCESS, NO_EVENT_VALUE)
          floatingPointId = it.stringResponse ?: ""
          registerRequest?.floatingPointId = floatingPointId
          registerRequest?.fpTag = registerRequest?.ProfileProperties?.domainName
          registerRequest?.profileId = response.result?.loginId
          apiBusinessActivatePlan(floatingPointId)
        }
      } else {
        hideProgress()
        showShortToast(getString(R.string.error_create_business_fp))
      }
    })
  }

  private fun apiBusinessActivatePlan(floatingPointId: String) {
    val request = getRequestPurchasedOrder(floatingPointId)
    viewModel?.postActivatePurchasedOrder(clientId, request)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      if (it.isSuccess()) {
        WebEngageController.trackEvent(ACTIVATE_FREE_PURCHASE_PLAN, SIGNUP_SUCCESS, NO_EVENT_VALUE)
      } else showShortToast(getString(R.string.unable_to_activate_business_plan))
      addFragmentReplace(com.framework.R.id.container, RegistrationSuccessFragment.newInstance(registerRequest!!), true)
    })
  }


  private fun getRequestPurchasedOrder(floatingPointId: String): ActivatePurchasedOrderRequest {
    val widList = ArrayList<PurchasedWidget>()
    registerRequest?.categoryDataModel?.sections?.forEach {
      it.getWidList().forEach { key ->
        val widget = PurchasedWidget(widgetKey = key, name = it.title, quantity = 1, desc = it.desc, recurringPaymentFrequency = "MONTHLY",
            isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0, netPrice = 0.0,
            consumptionConstraint = ConsumptionConstraint("DAYS", 30), images = ArrayList(),
            expiry = PurchasedExpiry("YEARS", 10))
        widList.add(widget)
      }
    }
    return ActivatePurchasedOrderRequest(clientId, floatingPointId, "EXTENSION", widList)
  }

  private fun getBusinessRequest(): BusinessCreateRequest {
    val createRequest = BusinessCreateRequest()
    createRequest.autoFillSampleWebsiteData = true
    createRequest.webTemplateId = registerRequest?.categoryDataModel?.webTemplateId
    createRequest.clientId = clientId
    createRequest.tag = registerRequest?.ProfileProperties?.domainName
    createRequest.name = registerRequest?.ProfileProperties?.businessName
    createRequest.address = ""
    createRequest.pincode = ""
    createRequest.country = "India"
    createRequest.primaryNumber = registerRequest?.ProfileProperties?.userMobile
    createRequest.email = registerRequest?.ProfileProperties?.userEmail
    createRequest.primaryNumberCountryCode = "+91"
    createRequest.uri = ""
    createRequest.primaryCategory = registerRequest?.categoryDataModel?.category_key
    createRequest.appExperienceCode = registerRequest?.categoryDataModel?.experience_code
//        createRequest.whatsAppNumber = registerRequest?.channelActionDatas?.firstOrNull()?.getNumberWithCode()
//        createRequest.whatsAppNotificationOptIn = registerRequest?.whatsappEntransactional ?: false
    createRequest.boostXWebsiteUrl = "www.${registerRequest?.ProfileProperties?.domainName?.toLowerCase(Locale.ROOT)}.nowfloats.com"
    return createRequest
  }
}