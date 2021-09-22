package com.inventoryorder.model.order.orderbottomsheet

import android.app.Activity
import com.inventoryorder.R
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import java.io.Serializable

data class OrderBottomSheet(
  var decription: String? = null,
  var items: ArrayList<BottomSheetOptionsItem>? = null,
  var title: String? = null,
) : Serializable {

  fun getPaymentStatusData(activity: Activity): ArrayList<BottomSheetOptionsItem> {
    val optionsList = ArrayList<BottomSheetOptionsItem>()
    val bottomSheetOptionsItem2 = BottomSheetOptionsItem()
    bottomSheetOptionsItem2.title = activity.getString(R.string.playment_already_received)
    bottomSheetOptionsItem2.description = activity.getString(R.string.customer_paid_via_cash_card_upi)
    bottomSheetOptionsItem2.displayValue = activity.getString(R.string.payment_received)
    bottomSheetOptionsItem2.serverValue = PaymentDetailsN.STATUS.SUCCESS.name
    val bottomSheetOptionsItem1 = BottomSheetOptionsItem()
    bottomSheetOptionsItem1.title = activity.getString(R.string.collect_later)
    bottomSheetOptionsItem1.description = activity.getString(R.string.send_payment_to_customer)
    bottomSheetOptionsItem1.displayValue = activity.getString(R.string.collect_later)
    bottomSheetOptionsItem1.isChecked = true
    bottomSheetOptionsItem1.serverValue = PaymentDetailsN.STATUS.PENDING.name
    optionsList.add(bottomSheetOptionsItem1)
    optionsList.add(bottomSheetOptionsItem2)
    return optionsList
  }
}