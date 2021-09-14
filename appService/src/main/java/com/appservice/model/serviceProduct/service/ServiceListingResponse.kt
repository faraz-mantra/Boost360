package com.appservice.model.serviceProduct.service

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceListingResponse(

  @field:SerializedName("StatusCode")
  val statusCode: Int? = null,

  @field:SerializedName("Result")
  val result: List<ResultItem?>? = null,
) : BaseResponse(), Serializable

data class ItemsItem(

  @field:SerializedName("ActionType")
  val actionType: String? = null,

  @field:SerializedName("Category")
  val category: String? = null,

  @field:SerializedName("Description")
  val description: String? = null,

  @field:SerializedName("SecondaryTileImages")
  val secondaryTileImages: List<String>? = null,

  @field:SerializedName("DiscountedPrice")
  val discountedPrice: Double? = null,

  @field:SerializedName("ActionTag")
  val actionTag: String? = null,

  @field:SerializedName("FeatureFileKey")
  val featureFileKey: String? = null,

  @field:SerializedName("Duration")
  val duration: Int? = null,

  @field:SerializedName("Image")
  val image: String? = null,

  @field:SerializedName("DiscountAmount")
  val discountAmount: Double? = null,

  @field:SerializedName("Name")
  val name: String? = null,

  @field:SerializedName("TileImage")
  val tileImage: String? = null,

  @field:SerializedName("BrandName")
  val brandName: String? = null,

  @field:SerializedName("Type")
  val type: Any? = null,

  @field:SerializedName("Price")
  val price: Double? = null,

  @field:SerializedName("Currency")
  val currency: String? = null,

  @field:SerializedName("_id")
  val id: String? = null,

  @field:SerializedName("SecondaryImages")
  val secondaryImages: List<String>? = null,

  ) : BaseResponse(), AppBaseRecyclerViewItem, Serializable {

  var recyclerViewItem: Int = RecyclerViewItemType.SERVICE_LISTING_VIEW.getLayout();

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun getLoaderItem(): ItemsItem {
    this.recyclerViewItem = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
    return this
  }

  fun getCategoryValue(): String {
    return if (category.isNullOrEmpty()) "No category" else category
  }
}

data class ResultItem(

  @field:SerializedName("Services")
  val services: Services? = null,

  @field:SerializedName("Category")
  val category: String? = null,
)

data class Services(

  @field:SerializedName("Items")
  val items: List<ItemsItem?>? = null,

  @field:SerializedName("Count")
  val count: Int? = null,
)
