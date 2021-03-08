package com.appservice.appointment.widgets

import android.view.View
import com.appservice.R
import com.appservice.appointment.ui.FragmentCustomerInvoiceSetup
import com.appservice.databinding.BottomSheetEnterGstDetailsBinding
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible

class BottomSheetEnterGSTDetails : BaseBottomSheetDialog<BottomSheetEnterGstDetailsBinding, AppointmentSettingsViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_enter_gst_details
    }

    var gstIn: (gst: String?) -> Unit = { }
    var businessName: (name: String?) -> Unit = { }

    override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
        return AppointmentSettingsViewModel::class.java
    }


    override fun onCreateView() {
        setOnClickListener(binding?.btnCancel, binding?.btnSaveChanges)
//        binding?.cetGst?.text =
//        binding?.cetBusinessName?.text =
        val parent = (requireParentFragment() as FragmentCustomerInvoiceSetup)
        parent.setGstData = {binding?.cetGst?.setText(it)}
        parent.setBusinessName = {binding?.cetBusinessName?.setText(it)}
        binding?.radioBusinessGst?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding?.ccbDeclearation?.gone()
                binding?.cetBusinessName?.visible()
                binding?.cetGst?.visible()
                binding?.ctvBusinessName?.visible()
                binding?.ctvGstTitle?.visible()
            }
        }
        binding?.radioNotRegistered?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding?.ccbDeclearation?.visible()
                binding?.cetBusinessName?.gone()
                binding?.cetGst?.gone()
                binding?.ctvBusinessName?.gone()
                binding?.ctvGstTitle?.gone()
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
                dismiss()
                gstIn(binding?.cetGst?.text.toString())
                businessName(binding?.cetBusinessName?.text.toString())
            }
        }
    }

}
