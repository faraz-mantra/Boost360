package com.appservice.appointment

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetEnterGstDetailsBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetEnterGSTDetails: BaseBottomSheetDialog<BottomSheetEnterGstDetailsBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_enter_gst_details
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
       return  BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnCancel,binding?.btnSaveChanges)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        dismiss()
    }
}