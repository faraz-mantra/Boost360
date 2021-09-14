package com.appservice.ecommercesettings.ui.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.appointment.model.PaymentResult
import com.appservice.appointment.ui.FragmentCustomerInvoiceSetup
import com.appservice.appointment.widgets.BottomSheetTaxInvoicesForPurchases
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetSetupTaxInvoicesForCustomerPurchaseBinding
import com.appservice.ecommercesettings.ui.FragmentEcommerceCustomerInvoiceSetup
import com.appservice.model.FileModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.isVisible
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.models.BaseViewModel
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class BottomEcommerceTaxInvoices : BaseBottomSheetDialog<BottomSheetSetupTaxInvoicesForCustomerPurchaseBinding, BaseViewModel>() {

  var clickType: (name: ClickType?) -> Unit = { }
  private var isEdit = false
  private var paymentProfileDetails: PaymentResult? = null
  var upiId: (name: String?) -> Unit = { }

  enum class ClickType {
    SAVECHANGES, CANCEL
  }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_setup_tax_invoices_for_customer_purchase
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnSaveChanges, binding?.btnCancel, binding?.btnClickPhoto)
    val parent = (requireParentFragment() as? FragmentEcommerceCustomerInvoiceSetup)
    this.paymentProfileDetails = arguments?.getSerializable(IntentConstant.PAYMENT_PROFILE_DETAILS.name) as? PaymentResult
    isEdit = paymentProfileDetails != null
    binding?.cetUpiId?.setText(paymentProfileDetails?.uPIId)
    val images = arguments?.getSerializable(IntentConstant.IMAGE_SIGNATURE.name) as ArrayList<FileModel>
    if (images.isNullOrEmpty().not()) setImage(images, parent = parent) else setImage(parent!!)
    if (paymentProfileDetails?.uPIId.isNullOrEmpty().not()) binding?.checkboxUpiId?.isChecked = true
    if (binding?.checkboxUpiId?.isChecked == true) binding?.cetUpiId?.visible() else binding?.cetUpiId?.gone()
    binding?.checkboxUpiId?.setOnCheckedChangeListener { buttonView, isChecked ->
      when (isChecked) {
        true -> binding?.cetUpiId?.visible()
        else -> binding?.cetUpiId?.gone()
      }
    }
    parent?.onImageClick = {
      val path = it[0].path
      setImage(it, path, parent)
    }
  }

  fun setImage(parent: FragmentEcommerceCustomerInvoiceSetup) {
    if (paymentProfileDetails?.merchantSignature == null) {
      binding?.btnClickPhoto?.visible()
      binding?.layoutImagePreview?.root?.gone()
    } else {
      binding?.btnClickPhoto?.gone()
      binding?.layoutImagePreview?.root?.visible()
      binding?.layoutImagePreview?.ctvSize?.text = if (paymentProfileDetails?.taxDetails?.gSTDetails?.documentName.isNullOrEmpty()) paymentProfileDetails?.merchantSignature?.split("/")
        ?.last() else paymentProfileDetails?.taxDetails?.gSTDetails?.documentName
      activity?.glideLoad(binding?.layoutImagePreview?.image, paymentProfileDetails?.merchantSignature)
    }
    imageClickListeners(parent)
  }

  private fun setImage(it: ArrayList<FileModel>, path: String? = it[0].path, parent: FragmentEcommerceCustomerInvoiceSetup? = null) {
    binding?.btnClickPhoto?.gone()
    binding?.layoutImagePreview?.root?.visible()
    binding?.layoutImagePreview?.ctvSize?.text = it[0].getFileName()
    activity?.glideLoad(binding?.layoutImagePreview?.image, path)
    imageClickListeners(parent)
  }

  private fun imageClickListeners(parent: FragmentEcommerceCustomerInvoiceSetup?) {
    binding?.layoutImagePreview?.crossIcon?.setOnClickListener {
      binding?.layoutImagePreview?.root?.gone()
      binding?.btnClickPhoto?.visible()
      parent?.clearImage()
    }
    binding?.layoutImagePreview?.cbChange?.setOnClickListener {
      parent?.clearImage()
      parent?.openImagePicker()
      paymentProfileDetails?.taxDetails?.gSTDetails?.documentName = null
      paymentProfileDetails?.merchantSignature = null

    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnCancel -> {
        dismiss()
        clickType(ClickType.CANCEL)
      }
      binding?.btnSaveChanges -> {
        if (isValid())
          setDataAndGoBack()
      }
      binding?.btnClickPhoto -> {
        (requireParentFragment() as FragmentEcommerceCustomerInvoiceSetup).openImagePicker()
      }
    }
  }

  fun isValid(): Boolean {
    if (binding?.btnClickPhoto?.isVisible() == true) {
      showLongToast(getString(R.string.please_choose_signature))
      return false
    } else {
      if (binding?.checkboxUpiId?.isChecked == true)
        if (validateUPI(binding?.cetUpiId?.text.toString()).not()) {
          showLongToast(getString(R.string.please_enter_valid_upi_id))
          return false
        }
    }
    return true
  }


  private fun setDataAndGoBack() {
    val upi = binding?.cetUpiId?.text.toString()
    paymentProfileDetails?.uPIId = if (binding?.checkboxUpiId?.isChecked == true) upi else null
    upiId(if (binding?.checkboxUpiId?.isChecked == true) upi else null)
    clickType(ClickType.SAVECHANGES)
    dismiss()
  }

  companion object {
    fun validateUPI(upi: String?): Boolean {
      val VALID_EMAIL_ADDRESS_REGEX: Pattern = Pattern.compile("^(.+)@(.+)$", Pattern.CASE_INSENSITIVE)
      val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(upi)
      return matcher.find()
    }
  }
}