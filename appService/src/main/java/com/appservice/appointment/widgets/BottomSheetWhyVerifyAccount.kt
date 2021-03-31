package com.appservice.appointment.widgets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetWhyDoWeNeedBankAccountBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetWhyVerifyAccount : BaseBottomSheetDialog<BottomSheetWhyDoWeNeedBankAccountBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_why_do_we_need_bank_account
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.understoodBtn)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.understoodBtn -> {
                dismiss()
            }
        }
    }
}