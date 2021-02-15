package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class ItemsItem(
    @SerializedName("Type")
    var type: String = "",
    @SerializedName("ProductDetails")
    var productDetails: ProductDetails,
    @SerializedName("Quantity")
    var quantity: Int = 0,
    @SerializedName("ProductOrOfferId")
    var productOrOfferId: String = ""
):Serializable , AppBaseRecyclerViewItem{

    var recyclerViewItem: Int = RecyclerViewItemType.PRODUCT_ITEM_SELECTED.getLayout()

    override fun getViewType(): Int {
        return recyclerViewItem
    }
}