package com.dashboard.controller.ui.business.bottomsheet

import android.view.View
import com.dashboard.R
import com.dashboard.databinding.BottomSheetFeaturedImageBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BusinessFeaturedBottomSheet:
    BaseBottomSheetDialog<BottomSheetFeaturedImageBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_featured_image
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        dialog.behavior.isDraggable = false
        setOnClickListener(binding?.btnUnderstood,binding?.rivCloseBottomSheet)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnUnderstood, binding?.rivCloseBottomSheet->{
                dismiss()
            }
        }
    }
}