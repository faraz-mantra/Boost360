package com.appservice.appointment

import com.appservice.R
import com.appservice.databinding.BottomSheetConfirmingChangesBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetConfirmingChange: BaseBottomSheetDialog<BottomSheetConfirmingChangesBinding, BaseViewModel>() {
    override fun getLayout(): Int {
       return R.layout.bottom_sheet_confirming_changes
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
       return BaseViewModel::class.java
    }

    override fun onCreateView() {
        TODO("Not yet implemented")
    }
}