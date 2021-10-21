package com.appservice.ui.domainbooking

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentConfirmingDomainBinding
import com.appservice.utils.WebEngageController
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*

class ConfirmingDomainFragment : AppBaseFragment<FragmentConfirmingDomainBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ConfirmingDomainFragment {
            val fragment = ConfirmingDomainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_confirming_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        WebEngageController.trackEvent(DOMAIN_CONFIRMING_DOMAIN_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
            resources.getString(
                R.string.confirming_domain
            ), resources.getDimensionPixelSize(R.dimen.size_44)
        )
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding?.btnConfirmApplyDomain?.setOnClickListener {
            WebEngageController.trackEvent(DOMAIN_CONFIRM_SELECTED_DOMAIN_CLICK, CLICK, NO_EVENT_VALUE)
            startFragmentDomainBookingActivity(
                activity = baseActivity,
                type = com.appservice.constant.FragmentType.ACTIVE_NEW_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }

        binding?.btnGoBack?.setOnClickListener {
            WebEngageController.trackEvent(DOMAIN_CONFIRMING_GO_BACK_BUTTON_CLICK, CLICK, NO_EVENT_VALUE)
            baseActivity.onNavPressed()
        }
    }
}