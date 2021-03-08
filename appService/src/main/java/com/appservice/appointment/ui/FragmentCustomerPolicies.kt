package com.appservice.appointment.ui

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentPoliciesForCustomerBinding
import com.framework.models.BaseViewModel

class FragmentCustomerPolicies: AppBaseFragment<FragmentPoliciesForCustomerBinding, BaseViewModel>() {
    override fun getViewModelClass(): Class<BaseViewModel> {
       return BaseViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.fragment_policies_for_customer    }
    companion object {
        fun newInstance(): FragmentCustomerPolicies {
            return FragmentCustomerPolicies()
        }
    }
}