package com.appservice.ui.updatesBusiness

import com.appservice.R
import com.appservice.databinding.BsheetUpdateDraftBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class UpdateDraftBSheet:BaseBottomSheetDialog<BsheetUpdateDraftBinding,BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.bsheet_update_draft
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }
}