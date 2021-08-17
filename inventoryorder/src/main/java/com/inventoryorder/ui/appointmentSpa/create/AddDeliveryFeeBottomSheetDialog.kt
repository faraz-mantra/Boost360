package com.inventoryorder.ui.appointmentSpa.create

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.databinding.*

class AddDeliveryFeeBottomSheetDialog(val deliveryFee : Double = 0.0, val type : String = "") : BaseBottomSheetDialog<BottomSheetAddDeliveryFeeBinding, BaseViewModel>() {

  var onClicked: (deliveryFeeValue: Double) -> Unit = { value : Double -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_add_delivery_fee
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  override fun onCreateView() {
    if (deliveryFee > 0) binding?.editDeliveryFee?.setText(deliveryFee.toString())

    if (type.isNotEmpty() && type.equals( AppConstant.TYPE_APPOINTMENT, true)) {
      binding?.tvSubTitle?.text = getString(R.string.enter_service_charges_to_add_to_billing)
      binding?.tvTitle?.text = getString(R.string.str_service_charges)
      binding?.editDeliveryFee?.hint = getString(R.string.enter_service_charges)
    }
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {

      binding?.buttonDone ->  {

        if (binding?.editDeliveryFee?.text?.isNullOrEmpty()?.not() == true) {
          onClicked(binding?.editDeliveryFee?.text?.toString()?.toDouble() ?: 0.0)
        } else {
          onClicked(0.0)
        }
      }
    }
  }
}