package com.inventoryorder.model.bottomsheet

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class DeliveryOptionsModel (val deliveryOptionSelectedIcon: Int? = null,
                            val deliveryOptionSelectedName: String? = null): AppBaseRecyclerViewItem {


    override fun getViewType(): Int {
        return RecyclerViewItemType.ITEM_DELIVERY_OPTIONS.getLayout()
    }

}