package com.appservice.model.catalog
//
//import com.appservice.R
//import com.appservice.constant.RecyclerViewItemType
//import com.appservice.recyclerView.AppBaseRecyclerViewItem
//import com.framework.base.BaseResponse
//import com.google.gson.annotations.SerializedName
//import java.io.Serializable
//
//data class CatalogTileModel(
//
//  @field:SerializedName("Tiles")
//  var tiles: ArrayList<TilesItem>? = null,
//) : BaseResponse()
//
//data class TilesItem(
//
//  @field:SerializedName("isEnabled")
//  var isEnabled: Boolean? = null,
//
//  @field:SerializedName("icon")
//  var icon: String? = null,
//
//  @field:SerializedName("description")
//  var description: String? = null,
//
//  @field:SerializedName("title")
//  var title: String? = null,
//  var recyclerViewItem: Int = RecyclerViewItemType.CATALOG_SETTING_TILES.getLayout()
//) : Serializable, AppBaseRecyclerViewItem {
//  override fun getViewType(): Int {
//    return recyclerViewItem
//  }
//}
