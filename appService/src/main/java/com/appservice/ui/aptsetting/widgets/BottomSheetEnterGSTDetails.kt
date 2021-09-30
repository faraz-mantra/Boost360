package com.appservice.ui.aptsetting.widgets

import BottomSheetRCM
import android.graphics.Paint
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.model.aptsetting.GSTDetails
import com.appservice.model.aptsetting.PaymentResult
import com.appservice.model.aptsetting.TaxDetails
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetEnterGstDetailsBinding
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import java.util.regex.Matcher
import java.util.regex.Pattern

class BottomSheetEnterGSTDetails : BaseBottomSheetDialog<BottomSheetEnterGstDetailsBinding, AppointmentSettingsViewModel>() {

  var isEdit = false
  var isECommerce = false
  private var paymentProfileDetails: PaymentResult? = null
  var gstIn: (gst: String?) -> Unit = { }
  var businessName: (name: String?) -> Unit = { }
  var clickType: (name: ClickType?) -> Unit = { }

  enum class ClickType {
    SAVECHANGES, CANCEL, NO_GST
  }

  fun setType(isECommerce: Boolean) {
    this.isECommerce = isECommerce
  }

  companion object {
    fun isValidGSTNo(str: String?): Boolean {
      val regex = ("^[0-9]{2}[A-Z]{5}[0-9]{4}"
          + "[A-Z]{1}[1-9A-Z]{1}"
          + "Z[0-9A-Z]{1}$")
      val p = Pattern.compile(regex)
      if (str == null) {
        return false
      }

      val m: Matcher = p.matcher(str)
      return m.matches()
    }
  }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_enter_gst_details
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }


  override fun onCreateView() {
    setOnClickListener(binding?.btnCancel, binding?.btnSaveChanges, binding?.whatsThis)
    this.paymentProfileDetails = arguments?.getSerializable(IntentConstant.PAYMENT_PROFILE_DETAILS.name) as PaymentResult
    if (paymentProfileDetails == null) isEdit = false
    if (paymentProfileDetails?.taxDetails?.gSTDetails?.gSTIN == "") {
      binding?.radioNotRegistered?.isChecked = true
      gstNotRegistered(true)
    } else {
      binding?.radioBusinessGst?.isChecked = true
      gstRegistered(true)
    }
    binding?.cetBusinessName?.setText(paymentProfileDetails?.taxDetails?.gSTDetails?.businessName)
    binding?.cetGst?.setText(paymentProfileDetails?.taxDetails?.gSTDetails?.gSTIN)
    binding?.radioBusinessGst?.setOnCheckedChangeListener { _, isChecked ->
      gstRegistered(isChecked)
    }
    binding?.radioNotRegistered?.setOnCheckedChangeListener { _, isChecked ->
      gstNotRegistered(isChecked)
    }
    binding?.whatsThis?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    binding?.whatsThis?.text = getString(R.string.what_s_this)
    binding?.gstRegisterCheck?.setOnCheckedChangeListener { _, isChecked ->
      binding?.btnSaveChanges?.isEnabled = isChecked
    }
  }

  private fun gstNotRegistered(isChecked: Boolean) {
    if (isChecked) {
      binding?.edtView?.gone()
      binding?.btnSaveChanges?.isEnabled = true
      binding?.btnSaveChanges?.text = getString(R.string.continue_)
      binding?.txtNote?.gone()
      binding?.rcmView?.gone()
      binding?.gstRegisterCheck?.visible()
    }
  }

  private fun gstRegistered(isChecked: Boolean) {
    if (isChecked) {
      binding?.edtView?.visible()
      binding?.btnSaveChanges?.isEnabled = true
      binding?.btnSaveChanges?.text = getString(R.string.save_changes)
      binding?.gstRegisterCheck?.gone()
      binding?.txtNote?.visible()
      if (this.isECommerce) binding?.rcmView?.visible() else binding?.rcmView?.gone()
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnCancel -> dismiss()
      binding?.btnSaveChanges -> {
        if (binding?.radioNotRegistered?.isChecked == false) {
          if (isValidGSTNo(binding?.cetGst?.text.toString())) {
            binding?.ctvInvalidGstin?.gone()
            binding?.cetGst?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rect_edit_txt)
            binding?.cetGst?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            setAndGoBack()
          } else {
            binding?.ctvInvalidGstin?.visible()
            binding?.cetGst?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_error_gstin)
            binding?.cetGst?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_appointment_settings, 0)
          }
        } else goback()
      }
      binding?.whatsThis -> {
        openRcmBottomSheet()
      }
    }
  }

  private fun openRcmBottomSheet() {
    val bottomSheetRCM = BottomSheetRCM()
    bottomSheetRCM.show(parentFragmentManager, BottomSheetRCM::javaClass.name)
  }

  private fun goback() {
    paymentProfileDetails?.taxDetails?.gSTDetails?.gSTIN = ""
    clickType(ClickType.NO_GST)
    dismiss()
  }

  private fun setAndGoBack() {
    if (paymentProfileDetails == null) paymentProfileDetails = PaymentResult()
    if (paymentProfileDetails?.taxDetails == null) paymentProfileDetails?.taxDetails = TaxDetails()
    if (paymentProfileDetails?.taxDetails?.gSTDetails == null) paymentProfileDetails?.taxDetails?.gSTDetails = GSTDetails();
    paymentProfileDetails?.taxDetails?.gSTDetails?.gSTIN = binding?.cetGst?.text.toString()
    paymentProfileDetails?.taxDetails?.gSTDetails?.businessName = binding?.cetBusinessName?.text.toString()
    gstIn(binding?.cetGst?.text.toString())
    businessName(binding?.cetBusinessName?.text.toString())
    clickType(ClickType.SAVECHANGES)
    dismiss()
  }
}
