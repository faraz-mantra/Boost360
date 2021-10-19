package com.appservice.ui.domainbooking

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentActiveDomainBinding
import com.appservice.model.domainBooking.DomainDetailsResponse
import com.appservice.utils.getMillisecondsToDate
import com.appservice.viewmodel.DomainBookingViewModel
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.clientId

class ActiveExistingDomainFragment : AppBaseFragment<FragmentActiveDomainBinding, DomainBookingViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ActiveExistingDomainFragment {
            val fragment = ActiveExistingDomainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(){
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
            resources.getString(
                R.string.website_domain
            ), resources.getDimensionPixelSize(R.dimen.size_44))

        domainDetailsApi()
    }

    override fun getLayout(): Int {
        return  R.layout.fragment_active_domain
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
        val string = domainDetailsResponse.domainName + domainDetailsResponse.domainType
        binding?.layoutIncludeDomainCard?.tvDomainValue?.text = string

        var activatedOn = domainDetailsResponse.activatedOn
        if (activatedOn?.contains("/Date")!!)
            activatedOn = activatedOn.replace("/Date(", "").replace(")/", "")

        var expiryOn = domainDetailsResponse.expiresOn
        if (expiryOn?.contains("/Date")!!)
            expiryOn = expiryOn.replace("/Date(", "").replace(")/", "")


        binding?.layoutIncludeDomainCard?.tvBookedValue?.text = getMillisecondsToDate(
            activatedOn.toLong(), "dd-MM-YYYY"
        )
        binding?.layoutIncludeDomainCard?.tvExpireValue?.text = getMillisecondsToDate(
            expiryOn.toLong(), "dd-MM-YYYY"
        )

        binding?.layoutIncludeDomainCard?.tvDomainStatus?.text = if (domainDetailsResponse.isActive!!)
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