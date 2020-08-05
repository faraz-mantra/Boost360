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

  var recyclerViewType = RecyclerViewItemType.SPECIFICATION_ITEM.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }

  fun data(): ArrayList<KeySpecification> {
    val list = ArrayList<KeySpecification>()
    for (i in 0..2) list.add(KeySpecification())
    return list
  }
}