package com.appservice.appointment

import com.appservice.R
import com.appservice.databinding.BottomSheetSuccessfullyPublishedBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetSuccessfullyUpdated: BaseBottomSheetDialog<BottomSheetSuccessfullyPublishedBinding, BaseViewModel>() {
    override fun getLayout(): Int {
      return  R.layout.bottom_sheet_successfully_published
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
       return BaseViewModel::class.java
    }

    override fun onCreateView() {
        TODO("Not yet implemented")
    }
}