package com.appservice.staffs.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class ServiceTimingModel(var name: String, var recyclerViewItemType: Int = RecyclerViewItemType.STAFF_SERVICE_TIMING_RECYCLER_ITEM.getLayout()) : AppBaseRecyclerViewItem,Serializable {
    override fun getViewType(): Int {
        return recyclerViewItemType
    }
}
