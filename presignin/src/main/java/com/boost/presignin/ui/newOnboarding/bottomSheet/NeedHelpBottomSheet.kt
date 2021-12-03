package com.boost.presignin.ui.newOnboarding.bottomSheet

import android.content.Intent
import android.net.Uri
import com.boost.presignin.R
import com.boost.presignin.databinding.SheetNeedHelpBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class NeedHelpBottomSheet : BaseBottomSheetDialog<SheetNeedHelpBinding, BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.sheet_need_help
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
            Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:" + getString(R.string.expert_contact_number))
                startActivity(this)
            }
        }
    }
}