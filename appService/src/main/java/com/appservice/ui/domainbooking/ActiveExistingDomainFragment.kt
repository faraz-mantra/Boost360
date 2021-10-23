package com.appservice.ui.domainbooking

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentActiveDomainBinding
import com.appservice.model.domainBooking.DomainDetailsResponse
import com.appservice.utils.WebEngageController
import com.appservice.utils.getMillisecondsToDate
import com.appservice.viewmodel.DomainBookingViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.clientId
import com.framework.webengageconstant.DOMAIN_ACTIVE_EXISTING_DOMAIN_DETAILS_PAGE_LOAD
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW

class ActiveExistingDomainFragment :
    AppBaseFragment<FragmentActiveDomainBinding, DomainBookingViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ActiveExistingDomainFragment {
            val fragment = ActiveExistingDomainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        WebEngageController.trackEvent(DOMAIN_ACTIVE_EXISTING_DOMAIN_DETAILS_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
        setOnClickListeners()
        domainDetailsApi()
    }

    private fun setOnClickListeners() {
        binding?.btnBuyAddon?.setOnClickListener {
            showShortToast(getString(R.string.coming_soon))
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_active_domain
    }

    override fun getViewModelClass(): Class<DomainBookingViewModel> {
        return DomainBookingViewModel::class.java
    }

    private fun domainDetailsApi() {
        viewModel?.domainDetails(sessionLocal.fpTag, clientId)?.observeOnce(this, {
            if (!it.isSuccess() || it == null) {
                showShortToast(getString(R.string.something_went_wrong))
                return@observeOnce
            }
            setAndParseData(it as DomainDetailsResponse)
        })
    }

    private fun setAndParseData(domainDetailsResponse: DomainDetailsResponse) {
        val domainName =
            if (!domainDetailsResponse.domainName.isNullOrEmpty() && domainDetailsResponse.domainName != "null")
                domainDetailsResponse.domainName
            else ""

        val domainType =
            if (domainDetailsResponse.domainType != null && domainDetailsResponse.domainType != "null")
                domainDetailsResponse.domainType
            else ""

        val domainFullName =
            if (domainName.isEmpty() && domainType.toString().isEmpty())
                "N/A"
            else
                domainName + domainType

        binding?.layoutIncludeDomainCard?.tvDomainValue?.text = domainFullName


        var activatedOn = domainDetailsResponse.activatedOn
        activatedOn = if (!activatedOn.isNullOrEmpty() && activatedOn.contains("/Date"))
            getMillisecondsToDate(
                activatedOn.replace("/Date(", "").replace(")/", "").toLong(),
                "dd-MM-YYYY"
            )
        else
            "N/A"
        binding?.layoutIncludeDomainCard?.tvBookedValue?.text = activatedOn

        var expiryOn = domainDetailsResponse.expiresOn
        expiryOn = if (!expiryOn.isNullOrEmpty() && expiryOn.contains("/Date"))
            getMillisecondsToDate(
                expiryOn.replace("/Date(", "").replace(")/", "").toLong(),
                "dd-MM-YYYY"
            )
        else
            "N/A"
        binding?.layoutIncludeDomainCard?.tvExpireValue?.text = expiryOn

        binding?.layoutIncludeDomainCard?.tvDomainStatus?.text =
            if (domainDetailsResponse.isActive!!)
                getString(R.string.active)
            else
                getString(R.string.expired)

        binding?.layoutIncludeDomainCard?.tvDomainStatus?.setBackgroundColor(
            if (domainDetailsResponse.isActive)
                getColor(R.color.green_light_1)
            else
                getColor(R.color.black_4a4a4a)
        )
    }
}