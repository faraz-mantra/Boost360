package com.appservice.ui.paymentgateway

import android.view.View
import com.appservice.R
import com.appservice.constant.RecyclerViewItemType
import com.appservice.databinding.BottomSheetConfirmKycBinding
import com.appservice.model.FileModel
import com.appservice.model.kycData.DataKyc
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.utils.getBitmap
import com.appservice.utils.getFileName
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.models.BaseViewModel
import java.io.File

class ConfirmKycBottomSheet : BaseBottomSheetDialog<BottomSheetConfirmKycBinding, BaseViewModel>() {
  private var panCarImage: File? = null
  private var bankStatementImage: File? = null
  private var additionalDocs: ArrayList<FileModel> = ArrayList()
  private var request: PaymentKycRequest? = null
  private var dataKyc: DataKyc? = null

  var onClicked: () -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_confirm_kyc
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(
    request: PaymentKycRequest?,
    panCarImage: File?,
    bankStatementImage: File?,
    additionalDocs: ArrayList<FileModel>,
    dataKyc: DataKyc?
  ) {
    this.request = request
    this.panCarImage = panCarImage
    this.bankStatementImage = bankStatementImage
    this.additionalDocs = additionalDocs
    this.dataKyc = dataKyc
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnSubmitSubmit, binding?.btnCancelReview)
    setDataView()
  }

  private fun setDataView() {
    if (panCarImage == null) {
      dataKyc?.panCardDocument?.let {
        activity?.glideLoad(
          binding?.ivPanCardImage!!,
          it,
          R.drawable.placeholder_image
        )
      }
    } else panCarImage?.getBitmap()?.let { binding?.ivPanCardImage?.setImageBitmap(it) }

    binding?.bankStatement?.text =
      if (bankStatementImage != null) bankStatementImage?.absolutePath?.getFileName() else dataKyc?.bankAccountStatement?.getFileName()

    binding?.headingPanNumber?.text = request?.actionData?.panNumber
    binding?.headingPanName?.text = request?.actionData?.nameOfPanHolder
    binding?.headingAccount?.text = "A/C No. ${request?.actionData?.bankAccountNumber}"
    binding?.tvBankBranchDetails?.text =
      "${request?.actionData?.nameOfBank} - ${request?.actionData?.bankBranchName}"

    if (additionalDocs.isNotEmpty()) {
      binding?.viewAddition?.visible()
      binding?.rvAdditionalDocs?.visible()
      setAdapter()
    } else {
      binding?.viewAddition?.gone()
      binding?.rvAdditionalDocs?.gone()
    }
  }

  private fun setAdapter() {
    val list = ArrayList<FileModel>()
    additionalDocs.forEach {
      list.add(
        FileModel(
          path = it.path,
          pathUrl = it.pathUrl,
          recyclerViewItem = RecyclerViewItemType.ADDITIONAL_FILE_VIEW.getLayout()
        )
      )
    }
    binding?.rvAdditionalDocs?.apply {
      val adapterImage = AppBaseRecyclerViewAdapter(baseActivity, list)
      adapter = adapterImage
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSubmitSubmit -> onClicked()
    }
    dismiss()
  }
}