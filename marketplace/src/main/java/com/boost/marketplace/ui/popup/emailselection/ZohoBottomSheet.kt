package com.boost.marketplace.ui.popup.emailselection

import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetZohoBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ZohoBottomSheet: BaseBottomSheetDialog<BottomSheetZohoBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.bottom_sheet_zoho
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }

}