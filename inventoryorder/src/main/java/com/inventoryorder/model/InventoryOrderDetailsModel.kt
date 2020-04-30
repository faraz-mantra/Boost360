package com.inventoryorder.model

import com.framework.base.BaseResponse
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class InventoryOrderDetailsModel(val itemImage: Int? = null,
                                 val itemName: String? = null,
                                 val itemQuantity: String? = null,
                                 val itemPrice: Int? = null,
                                 val itemDiscountedPrice: String? = null
) : BaseResponse(), AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.ITEM_ORDER_DETAILS.getLayout()
    }


    fun getOrderDetails(): ArrayList<InventoryOrderDetailsModel> {
        val list = ArrayList<InventoryOrderDetailsModel>()
        list.add(InventoryOrderDetailsModel(R.drawable.ic_mutton_rogan_josh, "Mutton Rogan Josh", "Qty: 2", 297, "297"))
        list.add(InventoryOrderDetailsModel(R.drawable.ic_mutton_rogan_josh, "Mutton Rogan Josh", "Qty: 2", 297, ""))
        list.add(InventoryOrderDetailsModel(R.drawable.ic_mutton_rogan_josh, "Mutton Rogan Josh", "Qty: 2", 297, ""))
        return list
    }


}