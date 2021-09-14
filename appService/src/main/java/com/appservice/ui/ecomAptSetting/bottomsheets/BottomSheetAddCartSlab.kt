package com.appservice.ui.ecomAptSetting.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetAddDeliverychargesBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetAddCartSlab : BaseBottomSheetDialog<BottomSheetAddDeliverychargesBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_add_deliverycharges
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnSaveChanges, binding?.btnCancel, binding?.ctvNoLimit1, binding?.ctvNoLimit2, binding?.ctvNoLimit3)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnCancel -> {
        dismiss()
      }
      binding?.ctvNoLimit1 -> {
        binding?.cetEnterLowerLimit?.setText(getString(R.string.no_lower_limit))
      }
      binding?.ctvNoLimit2 -> {
        binding?.cetEnterUpperLimit?.setText(R.string.no_upper_limit)

      }
      binding?.ctvNoLimit3 -> {
        binding?.cetDeliveryCharge?.setText(R.string.no_upper_limit)

      }
      binding?.btnSaveChanges -> {
        dismiss()
      }

    }
  }
}

data class CardSlab(
  var lowerLimit: String? = null,
  var upperLimit: String? = null,
  var charge: String? = null,
)