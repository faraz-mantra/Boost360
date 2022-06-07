package com.boost.marketplace.ui.details.domain

import com.boost.marketplace.R

import com.boost.marketplace.databinding.PopupSpecificatonExtensionCustomDomainBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class SSLCertificateBottomSheet : BaseBottomSheetDialog<PopupSpecificatonExtensionCustomDomainBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.popup_specificaton_extension_custom_domain
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