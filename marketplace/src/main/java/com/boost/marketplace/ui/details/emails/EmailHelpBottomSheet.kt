package com.boost.marketplace.ui.details.emails

import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetHelpEmailBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class EmailHelpBottomSheet : BaseBottomSheetDialog<BottomSheetHelpEmailBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.bottom_sheet_help_email
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }

}