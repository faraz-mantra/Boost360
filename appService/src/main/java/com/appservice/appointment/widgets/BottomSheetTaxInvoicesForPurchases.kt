package com.appservice.appointment.widgets

import android.view.View
import com.appservice.R
import com.appservice.appointment.ui.FragmentCustomerInvoiceSetup
import com.appservice.databinding.BottomSheetSetupTaxInvoicesForCustomerPurchaseBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.models.BaseViewModel

class BottomSheetTaxInvoicesForPurchases : BaseBottomSheetDialog<BottomSheetSetupTaxInvoicesForCustomerPurchaseBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_setup_tax_invoices_for_customer_purchase
    }
    var upiId: (name: String?) -> Unit = { }
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnSaveChanges, binding?.btnCancel, binding?.btnClickPhoto)
        val parent = (requireParentFragment() as FragmentCustomerInvoiceSetup)
        parent.secondaryImages = {
            val path = it[0].path
            binding?.btnClickPhoto?.gone()
            binding?.layoutImagePreview?.root?.visible()
            binding?.layoutImagePreview?.ctvSize?.text = it[0].getFileName()
            activity?.glideLoad(binding?.layoutImagePreview?.image, path)
            binding?.layoutImagePreview?.crossIcon?.setOnClickListener {
                binding?.layoutImagePreview?.root?.gone()
                binding?.btnClickPhoto?.visible()
                parent.clearImage()


            }
            binding?.layoutImagePreview?.cbChange?.setOnClickListener {
                parent.clearImage()
                parent.openImagePicker()
            }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnCancel -> {
                dismiss()
            }
            binding?.btnSaveChanges -> {
                upiId(binding?.cetUpiId?.text.toString())
                dismiss()
            }
            binding?.btnClickPhoto -> {
                (requireParentFragment() as FragmentCustomerInvoiceSetup).openImagePicker()
            }
        }
    }

}