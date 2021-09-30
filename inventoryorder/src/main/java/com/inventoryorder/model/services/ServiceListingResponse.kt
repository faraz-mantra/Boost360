package com.inventoryorder.model.services

import com.framework.base.BaseResponse
import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val DOCTOR_SERVICE_LIST_DATA="DOCTOR_SERVICE_LIST_DATA"


data class ServiceListingResponse(

  @field:SerializedName("StatusCode")
  val statusCode: Int? = null,
  @field:SerializedName("Result")
  val result: ArrayList<ResultItemService>? = null,
) : BaseResponse(), Serializable

data class ItemsItemService(

  @field:SerializedName("ActionType")
  val actionType: String? = null,

  @field:SerializedName("Category")
  val category: String? = null,

  @field:SerializedName("Description")
  val description: String? = null,

  @field:SerializedName("SecondaryTileImages")
  val secondaryTileImages: ArrayList<String>? = null,

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
  val secondaryImages: ArrayList<String>? = null,

  val isGeneralService: Boolean = false,

  ) : BaseResponse(), Serializable {


//  override fun getViewType(): Int {
//    return recyclerViewItem
//  }
//
//  fun getLoaderItem(): ItemsItem {
//    this.recyclerViewItem = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
//    return this
//  }

  fun getCategoryValue(): String {
    return  "SERVICE"
  }
}

data class ResultItemService(
  @field:SerializedName("Services")
  val services: Services? = null,
  @field:SerializedName("Category")
  val category: String? = null,
)

data class Services(
  @field:SerializedName("Items")
  val items: ArrayList<ItemsItemService>? = null,
  @field:SerializedName("Count")
  val count: Int? = null,
)

fun ArrayList<ItemsItemService>.saveDoctorServiceList() {
  PreferencesUtils.instance.saveData(DOCTOR_SERVICE_LIST_DATA, convertListObjToString(this) ?: "")
}

fun getDoctorServiceList(): ArrayList<ItemsItemService> {
  val resp = PreferencesUtils.instance.getData(DOCTOR_SERVICE_LIST_DATA, "") ?: ""
  return ArrayList(convertStringToList(resp) ?: ArrayList())
}
