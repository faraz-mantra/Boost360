package com.inventoryorder.model.spaAppointment.bookingslot.response

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class Staff(
  var AppointmentSlots: List<AppointmentSlot>? = null,
  var Description: Any? = null,
  var Image: String? = null,
  var IsAvailable: Boolean? = null,
  var Name: String? = null,
  var TileImageUrl: String? = null,
  var _id: String? = null,
  var isSelected: Boolean = false
) : Serializable, AppBaseRecyclerViewItem {
  var recyclerViewType = RecyclerViewItemType.STAFF_ITEM.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }
}