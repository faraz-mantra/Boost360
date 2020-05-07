package com.inventoryorder.model.bookingdetails

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class BookingsModel : AppBaseRecyclerViewItem, Serializable {

  override fun getViewType(): Int {
    return RecyclerViewItemType.BOOKINGS_ITEM_TYPE.getLayout()
  }

  fun getList(): ArrayList<BookingsModel> {
    val list = ArrayList<BookingsModel>()
    for (i in 0..4) list.add(BookingsModel())
    return list
  }

}