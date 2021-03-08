package com.appservice.appointment.ui

import android.view.View
import com.appservice.R
import com.appservice.appointment.widgets.BottomSheetEnterGSTDetails
import com.appservice.appointment.widgets.BottomSheetTaxInvoicesForPurchases
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

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.gstinContainer, binding?.editPurchases)

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.gstinContainer -> {
                showEnterBusinessGSTIN()
            }
            binding?.editPurchases -> {
                showTaxInvoicesForPurchases()
            }
        }
    }

    private fun showTaxInvoicesForPurchases() {
        val bottomSheetTaxInvoicesForPurchases = BottomSheetTaxInvoicesForPurchases()
        bottomSheetTaxInvoicesForPurchases.show(parentFragmentManager, BottomSheetTaxInvoicesForPurchases::class.java.name)
    }

    private fun showEnterBusinessGSTIN() {
        val bottomSheetEnterGSTDetails = BottomSheetEnterGSTDetails()
        bottomSheetEnterGSTDetails.show(parentFragmentManager, BottomSheetEnterGSTDetails::class.java.name)
    }
}