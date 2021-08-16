package com.appservice.ecommercesettings.ui

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentEcommerceSettingsBinding
import com.appservice.ui.catalog.startFragmentActivity
import com.framework.models.BaseViewModel

class FragmentEcommerceSettings : AppBaseFragment<FragmentEcommerceSettingsBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_ecommerce_settings
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentEcommerceSettings {
            return FragmentEcommerceSettings()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.catalogSetup, binding?.paymentCollectionSetup, binding?.customerInvoiceSetup, binding?.policiesForCustomer, binding?.deliverySetup)
        clearSearchFocus()
    }

    private fun clearSearchFocus() {
        // closes the soft keyboard when this fragment loads
        binding?.svSettings?.clearFocus()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.catalogSetup -> {
                startFragmentActivity(FragmentType.ECOMMERCE_CATALOG_SETTINGS)
            }
            binding?.paymentCollectionSetup -> {
                startFragmentActivity(FragmentType.ECOMMERCE_PAYMENT_SETTINGS)

            }
            binding?.customerInvoiceSetup -> {
                startFragmentActivity(FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_INVOICE)

            }
            binding?.policiesForCustomer -> {
                startFragmentActivity(FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_POLICIES)

            }
            binding?.deliverySetup -> {
                startFragmentActivity(FragmentType.ECOMMERCE_DELIVERY_CONFIG)

            }
        }
    }
}