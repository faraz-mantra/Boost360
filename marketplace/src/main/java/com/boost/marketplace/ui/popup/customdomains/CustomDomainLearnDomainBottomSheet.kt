package com.boost.marketplace.ui.popup.customdomains

import com.boost.marketplace.R
import com.boost.marketplace.databinding.PopupCustomdomainLearnDomainBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class CustomDomainLearnDomainBottomSheet : BaseBottomSheetDialog<PopupCustomdomainLearnDomainBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.popup_customdomain_learn_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

        binding?.backBtn?.setOnClickListener {
            dismiss()
        }
    }

}