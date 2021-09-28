package com.appservice.ui.domainbooking

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentActiveNewDomainBinding
import com.appservice.model.domainBooking.DomainDetailsResponse
import com.appservice.utils.getMillisecondsToDate
import com.appservice.viewmodel.DomainBookingViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.clientId

class ActiveNewDomainFragment :
    AppBaseFragment<FragmentActiveNewDomainBinding, DomainBookingViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ActiveNewDomainFragment {
            val fragment = ActiveNewDomainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
            resources.getString(
                R.string.website_domain
            ), resources.getDimensionPixelSize(R.dimen.size_44)
        )

        domainDetailsApi()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_active_new_domain
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
        binding?.layoutDomainDetails?.tvDomainValue?.text =string

        binding?.layoutDomainDetails?.tvBookedValue?.text = getMillisecondsToDate(
            domainDetailsResponse.activatedOn as Long, "dd-MM-YYYY"
        )
        binding?.layoutDomainDetails?.tvExpireValue?.text = getMillisecondsToDate(
            domainDetailsResponse.expiresOn as Long, "dd-MM-YYYY"
        )
    }
}