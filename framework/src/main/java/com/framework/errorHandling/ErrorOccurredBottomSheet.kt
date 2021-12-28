package com.framework.errorHandling

import android.content.DialogInterface
import com.framework.R
import com.framework.base.BaseBottomSheetDialog
import com.framework.databinding.BsheetErrorOccurredBinding
import com.framework.models.BaseViewModel

class ErrorOccurredBottomSheet(val errorCode: String?) : BaseBottomSheetDialog<BsheetErrorOccurredBinding, BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.bsheet_error_occurred
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.btnReportAnError?.setOnClickListener {
            dismiss()
            ReportIssueBottomSheet(errorCode).show(parentFragmentManager, ReportIssueBottomSheet::class.java.name)
        }

        binding?.btnTryAgain?.setOnClickListener {
            finishWithActivity()
        }

        binding?.ivClose?.setOnClickListener {
            finishWithActivity()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        baseActivity.finish()
    }

    fun finishWithActivity(){
        dismiss()
        baseActivity.finish()
    }
}