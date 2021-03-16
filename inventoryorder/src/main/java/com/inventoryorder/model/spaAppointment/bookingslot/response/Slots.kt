package com.inventoryorder.model.spaAppointment.bookingslot.response

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class Slots(
    var EndTime: String ?= null,
    var IsAvailable: Boolean ?= null,
    var StartTime: String ?= null,
    var _id: String ?= null,
    var isSelected : Boolean = false
) : Serializable, AppBaseRecyclerViewItem {
    var recyclerViewType = RecyclerViewItemType.SLOTS_ITEM.getLayout()
    override fun getViewType(): Int {
        return recyclerViewType
    }
}