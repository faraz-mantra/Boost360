package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import android.widget.CompoundButton
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetCancelOrderBinding
import com.inventoryorder.databinding.BottomSheetDeliveryTypeBinding
import com.inventoryorder.databinding.BottomSheetPaymentModeBinding
import com.inventoryorder.model.ordersdetails.OrderItem

class PaymentModeBottomSheetDialog : BaseBottomSheetDialog<BottomSheetPaymentModeBinding, BaseViewModel>() {

  private var cancellingEntity: String? = OrderItem.CancellingEntity.BUYER.name
  private var orderItem: OrderItem? = null
  var onClicked: (cancellingEntity: String,reasonText:String) -> Unit = { _: String, _: String -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_payment_mode
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    binding?.radioPaymentLink?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
      override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
          binding?.radioPrepaid?.isChecked = false
          binding?.radioCod?.isChecked = false
        }
      }
    })

    binding?.radioPrepaid?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
      override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
          binding?.radioPaymentLink?.isChecked = false
          binding?.radioCod?.isChecked = false
        }
      }
    })

    binding?.radioCod?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
      override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
          binding?.radioPaymentLink?.isChecked = false
          binding?.radioPrepaid?.isChecked = false
        }
      }
    })
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
     // binding?.buttonDone -> onClicked(cancellingEntity?:"", (binding?.txtReason?.text?.toString()?:""))
    }
  }

}