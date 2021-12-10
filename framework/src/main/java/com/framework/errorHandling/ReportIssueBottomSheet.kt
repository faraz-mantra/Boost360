package com.framework.errorHandling

import android.os.Bundle
import com.framework.R
import com.framework.base.BaseBottomSheetDialog
import com.framework.databinding.BsheetReportAnIssueBinding
import com.framework.databinding.BsheetThankYouResponseBinding
import com.framework.models.BaseViewModel

class ReportIssueBottomSheet : BaseBottomSheetDialog<BsheetReportAnIssueBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): ReportIssueBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = ReportIssueBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_report_an_issue
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
    }
}