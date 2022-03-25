package com.boost.marketplace.ui.details.domain

import com.boost.marketplace.R
import com.boost.marketplace.databinding.LayoutSelectedNumberBottomsheetBinding
import com.boost.marketplace.databinding.PopupCallExpertCustomDomainBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class SelectedNumberBottomSheet : BaseBottomSheetDialog<LayoutSelectedNumberBottomsheetBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.layout_selected_number_bottomsheet
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }

}