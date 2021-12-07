package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import android.view.View
import com.festive.poster.R
import com.festive.poster.databinding.BsheetAddCaptionBinding
import com.festive.poster.databinding.BsheetDeleteDraftBinding
import com.festive.poster.databinding.BsheetEditPostBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class DeleteDraftBottomSheet : BaseBottomSheetDialog<BsheetDeleteDraftBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): DeleteDraftBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = DeleteDraftBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_delete_draft
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.btnYesDelete?.setOnClickListener {
            dismiss()
        }

        binding?.btnNoStayHere?.setOnClickListener {
            dismiss()
        }
    }
}