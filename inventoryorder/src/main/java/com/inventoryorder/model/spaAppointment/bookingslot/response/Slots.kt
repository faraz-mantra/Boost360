package com.inventoryorder.model.spaAppointment.bookingslot.response

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class Slots(
  var EndTime: String? = null,
  var IsAvailable: Boolean? = null,
  var StartTime: String? = null,
  var _id: String? = null,
  var isSelected: Boolean = false
) : Serializable, AppBaseRecyclerViewItem {
  var recyclerViewType = RecyclerViewItemType.SLOTS_ITEM_NEW.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }

  fun getTimeSlotText(): String {
    return "$StartTime - $EndTime"
  }

  fun getIcon(): Int? {
    return takeIf { isSelected }?.let { R.drawable.ic_option_selected }
      ?: R.drawable.ic_option_unselected
  }

  fun getColor(): Int {
    return takeIf { isSelected }?.let { R.color.khaki_light } ?: R.color.white
  }
}