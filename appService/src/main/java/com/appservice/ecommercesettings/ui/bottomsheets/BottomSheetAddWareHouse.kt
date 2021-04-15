package com.appservice.ecommercesettings.ui.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetAddWarehouseBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetAddWareHouse : BaseBottomSheetDialog<BottomSheetAddWarehouseBinding, BaseViewModel>() {
    var onClicked: (value: String) -> Unit = { }

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_add_warehouse
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnCancel, binding?.btnSaveChanges)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnSaveChanges -> {
                dismiss()
            }
            binding?.btnCancel -> {
                dismiss()
            }
        }
    }
}