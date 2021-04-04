package com.inventoryorder.ui.tutorials

import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetTutorialsOnAppointmentMgmtBinding

class TutorialsOnAppointmentMgmtBottomSheet : BaseBottomSheetDialog<BottomSheetTutorialsOnAppointmentMgmtBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_tutorials_on_appointment_mgmt
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }
}