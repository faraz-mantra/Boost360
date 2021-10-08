package com.appservice.ui.aptsetting.widgets

import com.appservice.R
import com.appservice.databinding.BottomSheetBankAccountVerifiedBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetAccountVerified: BaseBottomSheetDialog<BottomSheetBankAccountVerifiedBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_bank_account_verified
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }
}