package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.databinding.BsheetPostingProgressBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import java.util.*
import kotlin.concurrent.schedule

class PostingProgressBottomSheet :
    BaseBottomSheetDialog<BsheetPostingProgressBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): PostingProgressBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = PostingProgressBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_posting_progress
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        Timer("Progress Posting Update", false).schedule(2000) {
            dismiss()
            PostSuccessBottomSheet().show(baseActivity.supportFragmentManager, PostSuccessBottomSheet::class.java.name)
        }
    }
}