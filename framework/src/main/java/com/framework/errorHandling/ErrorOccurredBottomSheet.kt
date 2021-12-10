package com.framework.errorHandling

import android.os.Bundle
import com.framework.R
import com.framework.base.BaseBottomSheetDialog
import com.framework.databinding.BsheetErrorOccurredBinding
import com.framework.models.BaseViewModel

class ErrorOccurredBottomSheet : BaseBottomSheetDialog<BsheetErrorOccurredBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): ErrorOccurredBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = ErrorOccurredBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_error_occurred
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
    }
}