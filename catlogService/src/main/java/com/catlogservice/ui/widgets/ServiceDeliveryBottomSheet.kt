package com.catlogservice.ui.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catlogservice.R
import com.catlogservice.databinding.BottomSheetServiceDeliveryBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ServiceDeliveryBottomSheet() : BaseBottomSheetDialog<BottomSheetServiceDeliveryBinding, BaseViewModel>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_service_delivery
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.vwMyBusinessLocation, binding?.vwClientLocation, binding?.vwOtherVenue)
        setOnClickListener(binding?.btnDone, binding?.btnCancel)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.vwMyBusinessLocation -> {
                binding?.rbMyBusinessLocation?.isChecked = true
                binding?.rbClientLocation?.isChecked = false
                binding?.rbOtherVenue?.isChecked = false
            }
            binding?.vwClientLocation -> {
                binding?.rbMyBusinessLocation?.isChecked = false
                binding?.rbClientLocation?.isChecked = true
                binding?.rbOtherVenue?.isChecked = false
            }
            binding?.vwOtherVenue -> {
                binding?.rbMyBusinessLocation?.isChecked = false
                binding?.rbClientLocation?.isChecked = false
                binding?.rbOtherVenue?.isChecked = true
            }

            binding?.btnDone -> {
            }
            binding?.btnCancel -> {
                dismiss()
            }
        }
    }

}