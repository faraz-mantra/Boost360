package com.boost.marketplace.ui.popup.emailselection

import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetEmailConfirmationBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class EmailConfirmatinBottomSheet: BaseBottomSheetDialog<BottomSheetEmailConfirmationBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.bottom_sheet_email_confirmation
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }

}