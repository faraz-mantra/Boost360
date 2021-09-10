package com.appservice.appointment.widgets

import com.appservice.R
import com.appservice.constant.FragmentType
import com.appservice.databinding.BottomSheetVerificationUnderProcessBinding
import com.appservice.ui.catalog.startFragmentActivity
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetVerificationUnderProcess : BaseBottomSheetDialog<BottomSheetVerificationUnderProcessBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_verification_under_process
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.verificationBtn?.setOnClickListener {
            startFragmentActivity(FragmentType.APPOINTMENT_PAYMENT_SETTINGS,clearTop = true)
        }
    }
}