package com.inventoryorder.model.order.orderbottomsheet

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class BottomSheetOptionsItem(
  var description: String? = null,
  var displayValue: String? = null,
  var serverValue: String? = null,
  var title: String? = null,
  var isChecked: Boolean = false
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItem: Int = RecyclerViewItemType.PRODUCT_BOTTOM_SHEET_OPTIONS.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItem
  }
}