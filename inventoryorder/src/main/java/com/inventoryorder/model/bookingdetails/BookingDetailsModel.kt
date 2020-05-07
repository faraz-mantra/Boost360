package com.inventoryorder.model.bookingdetails

import com.framework.base.BaseResponse
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class BookingDetailsModel(val itemImage: Int? = null,
                          val itemName: String? = null,
                          val itemQuantity: String? = null,
                          val itemPrice: Int? = null,
                          val itemActualPrice: String? = null
) : BaseResponse(), AppBaseRecyclerViewItem{

    override fun getViewType(): Int {
       return RecyclerViewItemType.BOOKING_DETAILS.getLayout()
    }

    fun getOrderDetails(): ArrayList<BookingDetailsModel> {
        val list = ArrayList<BookingDetailsModel>()
        list.add(BookingDetailsModel(R.drawable.ic_mutton_rogan_josh, "Bridal ceretin Touch-up", null, 297, "297"))
        list.add(BookingDetailsModel(R.drawable.ic_mutton_rogan_josh, "Bridal ceretin Touch-up", null, 297, ""))
        return list
    }


}