package com.appservice.appointment.widgets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetEnterGstDetailsBinding
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible

class BottomSheetEnterGSTDetails : BaseBottomSheetDialog<BottomSheetEnterGstDetailsBinding, AppointmentSettingsViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_enter_gst_details
    }

    override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
        return AppointmentSettingsViewModel::class.java
    }



    override fun onCreateView() {
        setOnClickListener(binding?.btnCancel, binding?.btnSaveChanges)
        binding?.radioBusinessGst?.setOnCheckedChangeListener { buttonView, isChecked ->
            when (buttonView.id) {
                binding?.radioBusinessGst?.id -> {
                    binding?.ccbDeclearation?.gone()
                    binding?.cetBusinessName?.visible()
                    binding?.cetGst?.visible()
                    binding?.ctvBusinessName?.visible()
                    binding?.ctvGstTitle?.visible()
                }
                binding?.radioNotRegistered?.id -> {
                    binding?.ccbDeclearation?.visible()
                    binding?.cetBusinessName?.gone()
                    binding?.cetGst?.gone()
                    binding?.ctvBusinessName?.gone()
                    binding?.ctvGstTitle?.gone()

                }
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
            }
        }
    }
}