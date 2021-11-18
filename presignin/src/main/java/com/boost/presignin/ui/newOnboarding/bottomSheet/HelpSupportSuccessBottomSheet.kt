package com.boost.presignin.ui.newOnboarding.bottomSheet

import com.boost.presignin.R
import com.boost.presignin.databinding.SheetNeedHelpBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class HelpSupportSuccessBottomSheet : BaseBottomSheetDialog<SheetNeedHelpBinding, BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.sheet_help_success_support
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.ivClose?.setOnClickListener {
            dismiss()
        }

        binding?.tvCreateSupportRequest?.setOnClickListener {
            //TODO call API create support
        }
    }
}