package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class DeliveryModel(
  val deliveryOptionSelectedIcon: Int? = null,
  val deliveryOptionSelectedName: String? = null,
  var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ITEM_DELIVERY_OPTIONS.getLayout()
  }

  fun getIcon(): Int? {
    return takeIf { isSelected }?.let { R.drawable.ic_option_selected }
      ?: deliveryOptionSelectedIcon
  }

  fun getColor(): Int {
    return takeIf { isSelected }?.let { R.color.khaki_light } ?: R.color.white
  }

  fun getData(): ArrayList<DeliveryModel> {
    val list = ArrayList<DeliveryModel>()
    list.add(DeliveryModel(R.drawable.ic_option_unselected, "Delivery Option 1"))
    list.add(DeliveryModel(R.drawable.ic_option_unselected, "Delivery Option 2"))
    list.add(DeliveryModel(R.drawable.ic_option_unselected, "Delivery Option 3", true))
    list.add(DeliveryModel(R.drawable.ic_option_unselected, "Delivery Option 4"))
    list.add(DeliveryModel(R.drawable.ic_option_unselected, "Delivery Option 5"))
    list.add(DeliveryModel(R.drawable.ic_option_unselected, "Delivery Option 6"))
    return list
  }

}