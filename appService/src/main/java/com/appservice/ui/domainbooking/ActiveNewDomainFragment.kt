package com.appservice.ui.domainbooking

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
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

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
            )

        domainDetailsApi()
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
        binding?.layoutDomainDetails?.tvDomainValue?.text = string

        var activatedOn = domainDetailsResponse.activatedOn
        if (!activatedOn.isNullOrEmpty() && activatedOn?.contains("/Date")!!) {
            activatedOn = activatedOn.replace("/Date(", "").replace(")/", "")

            binding?.layoutDomainDetails?.tvBookedValue?.text = getMillisecondsToDate(
                activatedOn.toLong(), "dd-MM-YYYY"
            )
        }

        var expiryOn = domainDetailsResponse.expiresOn
        if (!expiryOn.isNullOrEmpty() && expiryOn.contains("/Date")) {
            expiryOn = expiryOn.replace("/Date(", "").replace(")/", "")

            binding?.layoutDomainDetails?.tvExpireValue?.text = getMillisecondsToDate(
                expiryOn.toLong(), "dd-MM-YYYY"
            )
        }

        binding?.layoutDomainDetails?.tvDomainStatus?.text = if (domainDetailsResponse.isActive!!)
            getString(R.string.active)
        else
            getString(R.string.expired)

        binding?.layoutDomainDetails?.tvDomainStatus?.setBackgroundColor(
            if (domainDetailsResponse.isActive)
                getColor(R.color.green_light_1)
            else
                getColor(R.color.black_4a4a4a)
        )
    }

    override fun getLayout(): Int {
        return R.layout.fragment_active_new_domain
    }

    override fun getViewModelClass(): Class<DomainBookingViewModel> {
        return DomainBookingViewModel::class.java
    }

}