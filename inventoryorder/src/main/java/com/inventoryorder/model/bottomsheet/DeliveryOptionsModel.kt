package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class DeliveryOptionsModel (val deliveryOptionSelectedIcon: Int? = null,
                            val deliveryOptionSelectedName: String? = null): AppBaseRecyclerViewItem {


    override fun getViewType(): Int {
        return RecyclerViewItemType.ITEM_DELIVERY_OPTIONS.getLayout()
    }

    private fun getData() : ArrayList<DeliveryOptionsModel>{

        val list = ArrayList<DeliveryOptionsModel>()
        list.add(DeliveryOptionsModel(R.drawable.ic_option_unselected,"Option comes here"))
        list.add(DeliveryOptionsModel(R.drawable.ic_option_unselected,"Option comes here"))
        list.add(DeliveryOptionsModel(R.drawable.ic_option_unselected,"Subtitle 2"))
        list.add(DeliveryOptionsModel(R.drawable.ic_option_unselected,"Option comes here"))
        list.add(DeliveryOptionsModel(R.drawable.ic_option_unselected,"Option comes here"))

        return list

    }

}