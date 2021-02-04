package com.appservice.staffs.ui.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomsheetInactiveStaffBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class InActiveBottomSheet : BaseBottomSheetDialog<BottomsheetInactiveStaffBinding, BaseViewModel>() {
    private var value: Boolean = true
    var onClicked: (value: Boolean) -> Unit = { }
    override fun getLayout(): Int {
        return R.layout.bottomsheet_inactive_staff
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnActivateStaff)

    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnActivateStaff -> {
                dismiss()
                onClicked(value)
            }
        }
    }
}