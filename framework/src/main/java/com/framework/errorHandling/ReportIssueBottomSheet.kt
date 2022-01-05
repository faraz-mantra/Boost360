package com.framework.errorHandling

import com.framework.R
import com.framework.base.NewBaseBottomSheetDialog
import com.framework.databinding.BsheetReportAnIssueBinding
import com.framework.extensions.afterTextChanged
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.rest.errorTicketGenerate.SalesAssistErrorRepository
import com.framework.utils.fromHtml

class ReportIssueBottomSheet(val errorCode: String?) : NewBaseBottomSheetDialog<BsheetReportAnIssueBinding, BaseViewModel>() {

  private lateinit var progressBottomSheet: ProgressBottomSheet

  override fun getLayout(): Int {
    return R.layout.bsheet_report_an_issue
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    progressBottomSheet = ProgressBottomSheet()
    binding?.tvErrorCode?.text = fromHtml("<font color=#4a4a4a>${getString(R.string.error_code)}</font> <font color=#eb5757>${errorCode ?: "N/A"}</font>")
    binding?.btnDone?.setOnClickListener { createErrorTicket(errorCode.toString()) }

    binding?.ivClose?.setOnClickListener { dismiss() }

    binding?.etErrorDesc?.afterTextChanged {
      binding?.tvWordCount?.text = (280 - it.length).toString()
      binding?.btnDone?.isEnabled = it.isNotEmpty()
    }
  }

  fun createErrorTicket(errorCode: String) {
    progressBottomSheet.show(baseActivity.supportFragmentManager, ProgressBottomSheet::class.java.name)
    SalesAssistErrorRepository.createErrorTicket(
      fpTag = sessionManager?.fpTag ?: "SHUBHAMCOACHING",
      subject = errorCode,
      comment = binding?.etErrorDesc?.text.toString(),
      submitterEmail = binding?.etErrorDesccasc?.text.toString()
    ).toLiveData().observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        progressBottomSheet.dismiss()
        ThankYouResponseBottomSheet().show(baseActivity.supportFragmentManager, ThankYouResponseBottomSheet::class.java.name)
        dismiss()
      } else {
        progressBottomSheet.dismiss()
        showShortToast(getString(R.string.places_try_again))
      }
    })
  }
}