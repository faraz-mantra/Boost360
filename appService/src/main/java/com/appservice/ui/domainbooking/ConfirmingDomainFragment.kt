package com.appservice.ui.domainbooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentActiveDomainBinding
import com.appservice.databinding.FragmentConfirmingDomainBinding
import com.framework.models.BaseViewModel

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
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
            resources.getString(
                R.string.confirming_domain
            ), resources.getDimensionPixelSize(R.dimen.size_44)
        )
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding?.btnConfirmApplyDomain?.setOnClickListener{
            startFragmentDomainBookingActivity(
                activity = baseActivity,
                type = com.appservice.constant.FragmentType.ACTIVE_NEW_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }
    }
}