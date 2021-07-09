package com.inventoryorder.model.timeSlot

import com.google.gson.annotations.SerializedName
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class TimeSlotData(
    @SerializedName("day")
    var day: String? = null,
    @SerializedName("startTime")
    var startTime: String? = null,
    @SerializedName("endTime")
    var endTime: String? = null,
    var isSelected: Boolean = false
) : Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.TIME_SLOT_ITEM.getLayout()
  }

  fun getTimeSlotText(): String {
    return "$startTime - $endTime"
  }

  fun getIcon(): Int? {
    return takeIf { isSelected }?.let { R.drawable.ic_option_selected } ?: R.drawable.ic_option_unselected
  }

  fun getColor(): Int {
    return takeIf { isSelected }?.let { R.color.khaki_light } ?: R.color.white
  }
}