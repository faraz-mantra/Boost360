package com.inventoryorder.model.bookingdetails

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class AllBookingsModel : AppBaseRecyclerViewItem, Serializable{

    override fun getViewType(): Int {
        return RecyclerViewItemType.ALL_BOOKINGS.getLayout()
    }


}