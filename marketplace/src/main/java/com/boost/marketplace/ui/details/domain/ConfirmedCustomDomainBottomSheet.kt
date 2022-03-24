package com.boost.marketplace.ui.details.domain

import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetHelpEmailBinding
import com.boost.marketplace.databinding.PopupCallExpertCustomDomainBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ConfirmedCustomDomainBottomSheet : BaseBottomSheetDialog<PopupCallExpertCustomDomainBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.popup_confirmed_custom_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }

}