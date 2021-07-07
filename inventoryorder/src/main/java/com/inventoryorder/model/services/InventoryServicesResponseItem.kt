package com.inventoryorder.model.services


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.services.KeySpecification
import com.inventoryorder.model.services.UniquePaymentUrl
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class InventoryServicesResponseItem(
  @SerializedName("ApplicationId")
  var applicationId: Any? = null,
  @SerializedName("availableUnits")
  var availableUnits: Double? = null,
  @SerializedName("brandName")
  var brandName: String? = null,
  @SerializedName("BuyOnlineLink")
  var buyOnlineLink: Any? = null,
  @SerializedName("category")
  var category: String? = null,
  @SerializedName("CreatedOn")
  var createdOn: String? = null,
  @SerializedName("CurrencyCode")
  var currencyCode: String? = null,
  @SerializedName("CustomWidgets")
  var customWidgets: Any? = null,
  @SerializedName("Description")
  var description: String? = null,
  @SerializedName("DiscountAmount")
  var discountAmount: Double? = null,
  @SerializedName("ExternalSourceId")
  var externalSourceId: Any? = null,
  @SerializedName("FPTag")
  var fPTag: String? = null,
  @SerializedName("GPId")
  var gPId: Any? = null,
  @SerializedName("GroupProductId")
  var groupProductId: Any? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("ImageUri")
  var imageUri: String? = null,
  @SerializedName("Images")
  var images: Any? = null,
  @SerializedName("IsArchived")
  var isArchived: Boolean? = null,
  @SerializedName("IsAvailable")
  var isAvailable: Boolean? = null,
  @SerializedName("isCodAvailable")
  var isCodAvailable: Boolean? = null,
  @SerializedName("IsFreeShipmentAvailable")
  var isFreeShipmentAvailable: Boolean? = null,
  @SerializedName("isPrepaidOnlineAvailable")
  var isPrepaidOnlineAvailable: Boolean? = null,
  @SerializedName("keySpecification")
  var keySpecification: KeySpecification? = null,
  @SerializedName("_keywords")
  var keywords: Any? = null,
  @SerializedName("maxCodOrders")
  var maxCodOrders: Int? = null,
  @SerializedName("maxPrepaidOnlineOrders")
  var maxPrepaidOnlineOrders: Int? = null,
  @SerializedName("MerchantName")
  var merchantName: Any? = null,
  @SerializedName("Name")
  var name: String? = null,
  @SerializedName("otherSpecifications")
  var otherSpecifications: List<Any>? = null,
  @SerializedName("paymentType")
  var paymentType: String? = null,
  @SerializedName("pickupAddressReferenceId")
  var pickupAddressReferenceId: String? = null,
  @SerializedName("Price")
  var price: Double? = null,
  @SerializedName("Priority")
  var priority: Int? = null,
  @SerializedName("ProductIndex")
  var productIndex: Int? = null,
  @SerializedName("productType")
  var productType: String? = null,
  @SerializedName("ProductUrl")
  var productUrl: String? = null,
  @SerializedName("sharedPlatforms")
  var sharedPlatforms: List<Any>? = null,
  @SerializedName("ShipmentDuration")
  var shipmentDuration: Int? = null,
  @SerializedName("tags")
  var tags: List<String>? = null,
  @SerializedName("TileImageUri")
  var tileImageUri: Any? = null,
  @SerializedName("TotalQueries")
  var totalQueries: Int? = null,
  @SerializedName("uniquePaymentUrl")
  var uniquePaymentUrl: UniquePaymentUrl? = null,
  @SerializedName("UpdatedOn")
  var updatedOn: String? = null,
  @SerializedName("variants")
  var variants: Boolean? = null
) : BaseResponse(), Serializable, AppBaseRecyclerViewItem {
  var isSelected: Boolean = false

  var recyclerViewType = RecyclerViewItemType.SERVICES_DEPARTMENT.getLayout()

  override fun getViewType(): Int {
    return recyclerViewType
  }

  fun getIcon(): Int {
    return if (isSelected) R.drawable.ic_selected_icon else R.drawable.ic_unselected_services
  }

  fun isAvailable(): Boolean {
    return isAvailable ?: false
  }

  fun price(): Double {
    return price ?: 0.0
  }

  fun discountAmount(): Double {
    return discountAmount ?: 0.0
  }

  fun discountedPrice(): Double {
    return if (price() > discountAmount()) (price() - discountAmount()) else 0.0
  }

  fun pickupAddressReferenceId(): String {
    return pickupAddressReferenceId ?: ""
  }

  fun getGeneralData(): InventoryServicesResponseItem {
    return InventoryServicesResponseItem(
      name = "General Consultation",
      discountAmount = 0.0,
      price = 0.0,
      isAvailable = true
    )
  }

  fun getType(): String {
    return if (id.isNullOrEmpty().not()) "PRODUCT" else "NO_ITEM"
  }
}