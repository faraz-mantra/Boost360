package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import android.view.View
import com.festive.poster.R
import com.festive.poster.databinding.BsheetAddCaptionBinding
import com.festive.poster.databinding.BsheetEditPostBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class CaptionBottomSheet : BaseBottomSheetDialog<BsheetAddCaptionBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): CaptionBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = CaptionBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_add_caption
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

        binding?.rivCloseBottomSheet?.setOnClickListener {
            dismiss()
        }

        binding?.tvDoneCaption?.setOnClickListener {
            dismiss()
        }
    }
}