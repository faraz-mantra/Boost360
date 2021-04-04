package com.inventoryorder.ui.tutorials

import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetLearnAboutAppointmentMgmtBinding

class LearnAboutAppointmentMgmtBottomSheet : BaseBottomSheetDialog<BottomSheetLearnAboutAppointmentMgmtBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_learn_about_appointment_mgmt
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }
}