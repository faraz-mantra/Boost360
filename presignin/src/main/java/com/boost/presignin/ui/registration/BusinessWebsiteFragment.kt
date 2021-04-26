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
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.utils.NetworkUtils
import java.util.*

open class BusinessWebsiteFragment : AppBaseFragment<FragmentBusinessWebsiteBinding, LoginSignUpViewModel>() {


    private var registerRequest: RequestFloatsModel? = null
    private var isDomain: Boolean = false
    private var domainValue: String? = null
    private var floatingPointId = ""
    private var isSyncCreateFpApi = false
//    private lateinit var progress: ProgressChannelDialog

    companion object {

        @JvmStatic
        fun newInstance(registerRequest: RequestFloatsModel) =
                BusinessWebsiteFragment().apply {
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
//    override fun showProgress(title: String?, cancelable: Boolean?) {
//        title?.let { progress.setTitle(it) }
//        cancelable?.let { progress.isCancelable = it }
//        activity?.let { progress.showProgress(it.supportFragmentManager) }
//    }
//
//    override fun hideProgress() {
//        progress.hideProgress()
//    }
    override fun onCreateView() {
        registerRequest = arguments?.getSerializable("request") as? RequestFloatsModel
//        progress = ProgressChannelDialog.newInstance()
        val websiteHint = registerRequest?.ProfileProperties?.businessName?.trim()?.replace(" ", "")
        val amountSpannableString = SpannableString("'$websiteHint' ").apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
        }

        val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
        backButton?.setOnClickListener {
            goBack()
        }
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
            apiHitCreateMerchantProfile()
        }
    }

    private fun goBack() {
        parentFragmentManager.popBackStackImmediate()
    }

    private fun apiHitCreateMerchantProfile() {
        showProgress()
        viewModel?.createMerchantProfile(request = registerRequest)?.observeOnce(viewLifecycleOwner, {
            hideProgress()
            val businessProfileResponse = it as? BusinessProfileResponse
            if (it.isSuccess() && businessProfileResponse != null) {
                showProgress()
                apiHitBusiness(businessProfileResponse)

            } else {
                showShortToast(getString(R.string.unable_to_create_profile))
            }
        })
    }


    private fun apiHitBusiness(businessProfileResponse: BusinessProfileResponse) {
        if (NetworkUtils.isNetworkConnected()) {
            putCreateBusinessOnboarding(businessProfileResponse)
        }

    }

    private fun putCreateBusinessOnboarding(businessProfileResponse: BusinessProfileResponse) {
        val request = getBusinessRequest()
        isSyncCreateFpApi = true
        viewModel?.putCreateBusinessOnboarding(businessProfileResponse.result?.loginId, request)?.observeOnce(viewLifecycleOwner, {
            hideProgress()
            if (it.status == 200 || it.status == 201 || it.status == 202) {
                if (it.stringResponse.isNullOrEmpty().not()) {
                    floatingPointId = it.stringResponse ?: ""
//                    saveFpCreateData()
                    registerRequest?.floatingPointId = floatingPointId
                    registerRequest?.fpTag = registerRequest?.ProfileProperties?.domainName
                    registerRequest?.profileId = businessProfileResponse.result?.loginId
                    apiBusinessActivatePlan(floatingPointId)


                }
            }
        })
    }

    private fun apiBusinessActivatePlan(floatingPointId: String) {
        showProgress()
        val request = getRequestPurchasedOrder(floatingPointId);
        viewModel?.postActivatePurchasedOrder(clientId, request)?.observeOnce(viewLifecycleOwner, {
            hideProgress()
            if (it.status == 200 || it.status == 201 || it.status == 202) {
                addFragmentReplace(com.framework.R.id.container, RegistrationSuccessFragment.newInstance(registerRequest!!), true);
            } else {
                showShortToast(getString(R.string.unable_to_activate_business_plan))
            }
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

private fun checkFpCreate(): Boolean {
    if (floatingPointId.isNotEmpty()) {
        return true
    }
    return false
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