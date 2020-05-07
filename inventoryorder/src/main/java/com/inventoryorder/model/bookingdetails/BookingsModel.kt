package com.inventoryorder.model.bookingdetails

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class BookingsModel(var date: String? = null) : AppBaseRecyclerViewItem, Serializable {

  var recyclerViewType = RecyclerViewItemType.BOOKINGS_ITEM_TYPE.getLayout()

  override fun getViewType(): Int {
    return recyclerViewType
  }


  fun getList(): ArrayList<BookingsModel> {
    val list = ArrayList<BookingsModel>()
    val dtToday = BookingsModel("Today")
    dtToday.recyclerViewType = RecyclerViewItemType.BOOKINGS_DATE_TYPE.getLayout()
    val dtYesterday = BookingsModel("Yesterday")
    dtYesterday.recyclerViewType = RecyclerViewItemType.BOOKINGS_DATE_TYPE.getLayout()
    list.add(dtToday)
    list.add(BookingsModel())
    list.add(BookingsModel())
    list.add(dtYesterday)
    list.add(BookingsModel())
    list.add(BookingsModel())
    list.add(BookingsModel())
    return list
  }

}