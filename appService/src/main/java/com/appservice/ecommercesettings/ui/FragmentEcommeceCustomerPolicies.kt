package com.appservice.ecommercesettings.ui

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentEcommercePoliciesForCustomersBinding
import com.framework.models.BaseViewModel

class FragmentEcommeceCustomerPolicies : AppBaseFragment<FragmentEcommercePoliciesForCustomersBinding, BaseViewModel>() {
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.fragment_ecommerce_policies_for_customers
    }

    companion object {
        fun newInstance(): FragmentEcommeceCustomerPolicies {
            return FragmentEcommeceCustomerPolicies()
        }
    }
}