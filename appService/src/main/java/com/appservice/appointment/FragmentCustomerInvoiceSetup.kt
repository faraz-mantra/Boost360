package com.appservice.appointment

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentCustomerInvoiceSetupBinding
import com.framework.models.BaseViewModel

class FragmentCustomerInvoiceSetup:AppBaseFragment<FragmentCustomerInvoiceSetupBinding,BaseViewModel> (){
    override fun getLayout(): Int {
        return R.layout.fragment_customer_invoice_setup
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object {
        fun newInstance(): FragmentCustomerInvoiceSetup {
            return FragmentCustomerInvoiceSetup()
        }
    }
}