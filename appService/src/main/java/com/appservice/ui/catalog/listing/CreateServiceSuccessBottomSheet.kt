package com.appservice.ui.catalog.listing

import com.appservice.R
import com.appservice.databinding.BottomSheetServiceCreatedSuccessfullyBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class CreateServiceSuccessBottomSheet : BaseBottomSheetDialog<BottomSheetServiceCreatedSuccessfullyBinding, BaseViewModel>() {

    var onClicked: (value: String) -> Unit = { }
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_service_created_successfully
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }


}