package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.databinding.BsheetPostSuccessBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class PostSuccessBottomSheet : BaseBottomSheetDialog<BsheetPostSuccessBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): PostSuccessBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = PostSuccessBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_post_success
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.ivClosePostSuccess?.setOnClickListener {
            dismiss()
        }
    }
}