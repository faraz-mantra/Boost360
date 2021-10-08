package com.appservice.ui.aptsetting.widgets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetCreateCategoryBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetCreateCategory : BaseBottomSheetDialog<BottomSheetCreateCategoryBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_create_category
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnSave, binding?.btnCancel)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnCancel -> {
                dismiss()
            }
            binding?.btnSave -> {
                dismiss()
            }
        }
    }
}