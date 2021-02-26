package com.appservice.appointment

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentPaymentSettingsBinding
import com.framework.models.BaseViewModel

class FragmentPaymentSettings : AppBaseFragment<FragmentPaymentSettingsBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_payment_settings
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentPaymentSettings {
            return FragmentPaymentSettings()
        }
    }
}