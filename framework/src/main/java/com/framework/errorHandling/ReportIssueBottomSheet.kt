package com.framework.errorHandling

import android.content.DialogInterface
import android.os.Bundle
import com.framework.R
import com.framework.base.BaseBottomSheetDialog
import com.framework.databinding.BsheetReportAnIssueBinding
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.rest.errorTicketGenerate.SalesAssistErrorRepository
import com.framework.utils.fromHtml

class ReportIssueBottomSheet(val errorCode: String?) : BaseBottomSheetDialog<BsheetReportAnIssueBinding, BaseViewModel>() {

    private lateinit var progressBottomSheet:ProgressBottomSheet

    override fun getLayout(): Int {
        return R.layout.bsheet_report_an_issue
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        progressBottomSheet = ProgressBottomSheet()
        binding?.tvErrorCode?.text = fromHtml("<font color=#4a4a4a>${getString(R.string.error_code)}</font> <font color=#eb5757>${errorCode?: "N/A"}</font>")
        binding?.btnDone?.setOnClickListener {
            createErrorTicket("Test Subject")
        }

        binding?.ivClose?.setOnClickListener {
            finishWithActivity()
        }
    }

    fun createErrorTicket(errorCode:String) {
        progressBottomSheet.show(parentFragmentManager, ProgressBottomSheet::class.java.name)
        SalesAssistErrorRepository.createErrorTicket(
            fpTag = sessionManager?.fpTag ?: "SHUBHAMCOACHING",
            subject = "",//errorCode,
            comment = binding?.etErrorDesc?.text.toString(),
            submitterEmail = binding?.etErrorDesccasc?.text.toString()
        ).toLiveData().observeOnce(viewLifecycleOwner, {
            if (it.isSuccess()) {
                progressBottomSheet.dismiss()
                dismiss()
                ThankYouResponseBottomSheet().show(parentFragmentManager, ThankYouResponseBottomSheet::class.java.name)
            } else {
                progressBottomSheet.dismiss()
                showShortToast(getString(R.string.places_try_again))
            }
        })
    }

    fun finishWithActivity(){
        dismiss()
        baseActivity.finish()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        baseActivity.finish()
    }
}