package com.appservice.staffs.ui.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomsheetRemoveStaffBottomSheetBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class RemoveStaffConfirmationBottomSheet : BaseBottomSheetDialog<BottomsheetRemoveStaffBottomSheetBinding, BaseViewModel>() {
    private var value: Boolean = true
    var onClicked: (value: Boolean) -> Unit = { }
    override fun getLayout(): Int {
        return R.layout.bottomsheet_remove_staff_bottom_sheet
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnDone, binding?.btnCancel)
        val isDoctor = arguments?.getBoolean(IntentConstant.STAFF_DATA.name, false) ?: false
        if (isDoctor){
            binding?.ctvHeadingRemoveStaff?.text = getString(R.string.delete_doctor)
            binding?.ctvAboutStaff?.text = getString(R.string.remove_the_doctor_will_remove_all_the_present)
            binding?.btnDone?.text = getString(R.string.btn_delete_text)
        }

    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnDone -> {
                onClicked(value)
                dismiss()
            }
            binding?.btnCancel -> {
                dismiss()
            }
        }
    }


}