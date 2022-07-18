package com.appservice.ui.aptsetting.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.model.aptsetting.InvoiceSetupRequest
import com.appservice.model.aptsetting.PaymentProfileResponse
import com.appservice.model.aptsetting.UpdateUPIRequest
import com.appservice.model.aptsetting.UploadMerchantSignature
import com.appservice.ui.aptsetting.widgets.BottomSheetConfirmGST
import com.appservice.ui.aptsetting.widgets.BottomSheetEnterGSTDetails
import com.appservice.ui.aptsetting.widgets.BottomSheetTaxInvoicesForPurchases
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentCustomerInvoiceSetupBinding
import com.appservice.model.FileModel
import com.appservice.rest.TaskCode
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.webengageconstant.*
import com.framework.webengageconstant.CUSTOMER_INVOICE_SETUP_LOAD

class FragmentCustomerInvoiceSetup : AppBaseFragment<FragmentCustomerInvoiceSetupBinding, AppointmentSettingsViewModel>() {

  private var data: PaymentProfileResponse? = null
  var setGstData: (gstin: String) -> Unit = {}
  var setBusinessName: (businessName: String) -> Unit = {}
  var onImageClick: (Image: ArrayList<FileModel>) -> Unit = { }
  private lateinit var bottomSheetTaxInvoicesForPurchases: BottomSheetTaxInvoicesForPurchases
  var imageList: ArrayList<FileModel> = ArrayList()

  companion object {
    fun newInstance(): FragmentCustomerInvoiceSetup {
      return FragmentCustomerInvoiceSetup()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_customer_invoice_setup
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    sessionLocal = UserSessionManager(requireActivity())
    WebEngageController.trackEvent(CUSTOMER_INVOICE_SETUP_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.editPurchases?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    binding?.editPurchases?.text = getString(R.string.edit_)
    setOnClickListener(binding?.gstinContainer, binding?.invoiceSetupContainer, binding?.viewSampleInvoice)
    getProfileDetails()
  }

  private fun getProfileDetails(isShowProgress: Boolean = true) {
    if (isShowProgress) showProgress()
    hitApi(viewModel?.getPaymentProfileDetails(sessionLocal.fPID, clientId = clientId), (R.string.error_getting_payment_details))
  }

  override fun onSuccess(it: BaseResponse) {
    super.onSuccess(it)
    hideProgress()
    when (it.taskcode) {
      TaskCode.GET_PAYMENT_PROFILE_DETAILS.ordinal -> {
        if (data == null) data = PaymentProfileResponse()
        this.data = it as PaymentProfileResponse
        val gSTIN = data?.result?.taxDetails?.gSTDetails?.gSTIN
        val businessName = data?.result?.taxDetails?.gSTDetails?.businessName
        if (gSTIN.isNullOrEmpty().not() || businessName.isNullOrEmpty().not()) {
          setGstData(gSTIN?:"")
          setBusinessName(businessName?:"")
        }
        updatePreviousData()
      }
      TaskCode.SETUP_INVOICE.ordinal -> {
        getProfileDetails(false)
        showShortToast(getString(R.string.gst_details_updated))
      }
      TaskCode.PUT_MERCHANT_SIGNATURE.ordinal -> {
        getProfileDetails(false)
        showShortToast(getString(R.string.signature_uploaded))
      }
      TaskCode.ADD_MERCHANT_UPI.ordinal -> {
        getProfileDetails(false)
        showShortToast(getString(R.string.merchant_upi_added))
      }
    }
  }

  override fun onFailure(it: BaseResponse) {
    super.onFailure(it)
    hideProgress()
  }

  private fun updatePreviousData() {
    if (data?.result?.taxDetails?.gSTDetails?.gSTIN.isNullOrEmpty()) {
      binding?.hintEnterGst?.visible()
      binding?.ctvGstinHeading?.text = getString(R.string.gstin_)
    } else {
      binding?.ctvGstinHeading?.text = getString(R.string.gstin_)
      binding?.ctvGstNum?.text = data?.result?.taxDetails?.gSTDetails?.gSTIN
      binding?.icDone?.visible()
      binding?.hintEnterGst?.gone()
    }
    if (data?.result?.uPIId.isNullOrEmpty() || data?.result?.uPIId == "null") {
      binding?.upiIdHeading?.gone()
      binding?.upiId?.gone()
      binding?.divider3?.gone()
    } else {
      binding?.upiIdHeading?.visible()
      binding?.upiId?.visible()
      binding?.upiId?.text = data?.result?.getUpiId()
      binding?.divider3?.visible()

    }
    if (data?.result?.taxDetails?.gSTDetails?.businessName.isNullOrEmpty()) {
      binding?.ctvCompanyName?.gone()
      binding?.ctvCompanyNameHeading?.gone()
    } else {
      binding?.ctvCompanyName?.visible()
      binding?.ctvCompanyNameHeading?.visible()
      binding?.ctvCompanyName?.text = data?.result?.taxDetails?.gSTDetails?.businessName
    }
    if (data?.result?.merchantSignature.isNullOrEmpty().not()) {
      binding?.icDoneImg?.visible()
      binding?.signatureHeading?.visible()
      binding?.signature?.visible()
    } else {
      binding?.icDoneImg?.gone()
      binding?.signatureHeading?.gone()
      binding?.signature?.gone()
    }
    val isUpdated = (data?.result?.taxDetails?.gSTDetails?.gSTIN.isNullOrEmpty() && (data?.result?.uPIId.isNullOrEmpty() || data?.result?.uPIId == "null") && data?.result?.merchantSignature.isNullOrEmpty()).not()
    onBusinessVerificationAddedOrUpdated(isUpdated)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.gstinContainer -> {
        WebEngageController.trackEvent(BUSINESS_GSTIN_CLICK, CLICK, NO_EVENT_VALUE)
        showEnterBusinessGSTIN()
      }
      binding?.invoiceSetupContainer -> {
        WebEngageController.trackEvent(TAX_INVOICE_CUSTOMER, CLICK, NO_EVENT_VALUE)
        showTaxInvoicesForPurchases()
      }
      binding?.viewSampleInvoice -> {
      }
      binding?.btnSaveDetails -> {
      }
    }
  }

  private fun showTaxInvoicesForPurchases() {
    this.bottomSheetTaxInvoicesForPurchases = BottomSheetTaxInvoicesForPurchases()
    bottomSheetTaxInvoicesForPurchases.upiId = { binding?.upiId?.text = it.toString() }
    bottomSheetTaxInvoicesForPurchases.clickType = {
      if (it == BottomSheetTaxInvoicesForPurchases.ClickType.SAVECHANGES) {
//                hitApi(viewModel?.invoiceSetup(InvoiceSetupRequest(panDetails = null, gSTDetails = data?.result?.taxDetails?.gSTDetails, tanDetails = null, clientId = UserSession.clientId, floatingPointId = UserSession.fpId)), (R.string.error_updating_gst_details))
        hitApi(viewModel?.addMerchantUPI(UpdateUPIRequest(clientId, uPIId = binding?.upiId?.text.toString(), sessionLocal.fPID)), (R.string.error_updating_upi_id))
        if (imageList.isNotEmpty()) {
          showProgress()
          hitApi(
            liveData = viewModel?.uploadSignature(
              UploadMerchantSignature(
                "png", Base64.encodeToString(imageList[0].getFile()?.readBytes(), Base64.DEFAULT),
                clientId = clientId, sessionLocal.fPID, imageList[0].getFileName()
              )
            ), errorStringId = (R.string.error_updating_upi_id)
          )
        }
      }
      if (it == BottomSheetTaxInvoicesForPurchases.ClickType.CANCEL) {
      }
    }
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.PAYMENT_PROFILE_DETAILS.name, data?.result)
    bundle.putSerializable(IntentConstant.IMAGE_SIGNATURE.name, imageList)
    bottomSheetTaxInvoicesForPurchases.arguments = bundle
    bottomSheetTaxInvoicesForPurchases.show(childFragmentManager, BottomSheetTaxInvoicesForPurchases::class.java.name)
  }

  private fun showEnterBusinessGSTIN() {
    val bottomSheetEnterGSTDetails = BottomSheetEnterGSTDetails()
    bottomSheetEnterGSTDetails.businessName = {
      binding?.ctvCompanyName?.text = it
      binding?.ctvCompanyName?.visible()
      binding?.ctvCompanyNameHeading?.visible()
      binding?.hintEnterGst?.gone()
    }
    bottomSheetEnterGSTDetails.gstIn = { gst ->
      binding?.ctvGstNum?.text = gst
      binding?.ctvCompanyName?.gone()
      binding?.ctvCompanyNameHeading?.gone()
      binding?.hintEnterGst?.visible()
    }
    bottomSheetEnterGSTDetails.clickType = {
      if (it == BottomSheetEnterGSTDetails.ClickType.SAVECHANGES) {
        openConfirmGstBottomSheet()
      }
    }
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.PAYMENT_PROFILE_DETAILS.name, data?.result)
    bottomSheetEnterGSTDetails.arguments = bundle
    bottomSheetEnterGSTDetails.show(childFragmentManager, BottomSheetEnterGSTDetails::class.java.name)
  }

  private fun openConfirmGstBottomSheet() {
    val bottomSheetConfirmGST = BottomSheetConfirmGST()
    val bundle = Bundle()
    bundle.putString(IntentConstant.GSTIN.name, data?.result?.taxDetails?.gSTDetails?.gSTIN)
    bundle.putString(IntentConstant.BUSINESSNAME.name, data?.result?.taxDetails?.gSTDetails?.businessName)
    bottomSheetConfirmGST.arguments = bundle
    bottomSheetConfirmGST.clickType = {
      if (it == BottomSheetConfirmGST.ClickType.SAVECHANGES) {
        showProgress()
        hitApi(
          viewModel?.invoiceSetup(
            InvoiceSetupRequest(
              panDetails = null, gSTDetails = data?.result?.taxDetails?.gSTDetails,
              tanDetails = null, clientId = clientId, floatingPointId = sessionLocal.fPID
            )
          ), (R.string.error_updating_gst_details)
        )
      }
      if (it == BottomSheetConfirmGST.ClickType.CANCEL) {
      }
    }
    bottomSheetConfirmGST.show(childFragmentManager, BottomSheetConfirmGST::class.java.name)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
      clearImage()
      secondaryImage(mPaths)
    }
  }

  fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  private fun openImagePicker(it: ClickType) {
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(requireActivity())
      .mode(type)
      .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
      .extension(ImagePicker.Extension.PNG).allowMultipleImages(true)
      .scale(800, 800)
      .enableDebuggingMode(true).build()
  }

  private fun secondaryImage(mPaths: ArrayList<String>) {
    if (imageList.size < 1) {
      if (mPaths.size + imageList.size > 1) showLongToast(resources.getString(R.string.only_one_file_is_allowed))
      var index: Int = imageList.size
      while (index < 1 && mPaths.isNotEmpty()) {
        imageList.add(FileModel(path = mPaths[0]))
        mPaths.removeAt(0)
        index++
      }
      onImageClick(imageList)
    } else showLongToast(getString(R.string.only_one_file_is_allowed))
  }

  fun clearImage() {
    imageList.clear()
  }
}

