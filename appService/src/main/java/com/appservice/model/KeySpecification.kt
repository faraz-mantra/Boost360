package com.appservice.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KeySpecification(
    @SerializedName("key")
    var key: String? = null,
    @SerializedName("value")
    var value: String? = null
) : AppBaseRecyclerViewItem, Serializable {

//  @Transient
var recyclerViewType = RecyclerViewItemType.SPECIFICATION_ITEM.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }

}