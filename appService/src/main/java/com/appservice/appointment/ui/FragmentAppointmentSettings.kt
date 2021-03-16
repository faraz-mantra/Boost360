package com.appservice.appointment.ui

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentAppointmentSettingsBinding
import com.appservice.ui.catalog.startFragmentActivity
import com.framework.models.BaseViewModel

class FragmentAppointmentSettings: AppBaseFragment<FragmentAppointmentSettingsBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_appointment_settings
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentAppointmentSettings {
            return FragmentAppointmentSettings()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.catalogSetup, binding?.paymentCollectionSetup, binding?.customerInvoiceSetup, binding?.policiesForCustomer)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.catalogSetup->{
                startFragmentActivity(FragmentType.APPOINTMENT_CATALOG_SETTINGS)
            }
            binding?.paymentCollectionSetup->{
                startFragmentActivity(FragmentType.APPOINTMENT_PAYMENT_SETTINGS)

            }
            binding?.customerInvoiceSetup->{
                startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE)

            }
            binding?.policiesForCustomer->{
                startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES)

            }
        }
    }
}