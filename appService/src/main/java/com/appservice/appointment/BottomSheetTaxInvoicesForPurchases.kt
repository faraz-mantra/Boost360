package com.appservice.appointment

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetSetupTaxInvoicesForCustomerPurchaseBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetTaxInvoicesForPurchases : BaseBottomSheetDialog<BottomSheetSetupTaxInvoicesForCustomerPurchaseBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_setup_tax_invoices_for_customer_purchase
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnSaveChanges, binding?.btnCancel)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        dismiss()
    }
}