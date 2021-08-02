package com.inventoryorder.model.order

import com.framework.base.BaseResponse
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable
import java.util.*

data class ProductItem(
  val ApplicationId: String? = null,
  val BuyOnlineLink: String? = null,
  val CreatedOn: String? = null,
  val CurrencyCode: String? = null,
  val CustomWidgets: Any? = null,
  val Description: String? = null,
  val DiscountAmount: Double? = null,
  val ExternalSourceId: String? = null,
  val FPTag: String? = null,
  val GPId: String? = null,
  val GroupProductId: String? = null,
  val ImageUri: String? = null,
  val Images: Any? = null,
  val IsArchived: Boolean? = null,
  val IsAvailable: Boolean? = null,
  val IsFreeShipmentAvailable: Boolean? = null,
  val MerchantName: String? = null,
  val Name: String? = null,
  val Price: Double? = null,
  val Priority: Int? = null,
  val ProductIndex: Int? = null,
  val ProductUrl: String? = null,
  val ShipmentDuration: Int? = null,
  val TileImageUri: String? = null,
  val TotalQueries: Int? = null,
  val UpdatedOn: String? = null,
  val _id: String? = null,
  val _keywords: Any? = null,
  val availableUnits: Double? = null,
  val brandName: String? = null,
  val category: String? = null,
  val gstSlab: Double? = null,
  val hsnCode: String? = null,
  val isCodAvailable: Boolean? = null,
  val isPrepaidOnlineAvailable: Boolean? = null,
  val keySpecification: KeySpecification? = null,
  val maxCodOrders: Int? = null,
  val maxPrepaidOnlineOrders: Int? = null,
  val otherSpecifications: List<Any>? = null,
  val paymentType: String? = null,
  val pickupAddressReferenceId: Any? = null,
  val productType: String? = null,
  val tags: List<String>? = null,
  val uniquePaymentUrl: UniquePaymentUrl? = null,
  val variants: Boolean? = null,

  var isProductAddedInCart: Boolean = false,
  var productQuantityAdded: Int = 0,
) : BaseResponse(), AppBaseRecyclerViewItem, Serializable {
  var recyclerViewType = RecyclerViewItemType.PRODUCT_ITEM.getLayout()

  override fun getViewType(): Int {
    return recyclerViewType
  }

  fun getNameValue(): String {
    return (Name ?: "").toLowerCase(Locale.ROOT)
  }


  fun getCurrencyCodeValue(): String {
    return if (CurrencyCode.isNullOrEmpty().not()) CurrencyCode!! else "INR"
  }

  fun getActualPrice(): Double {
    return Price ?: 0.0
  }

  fun getDiscountAmount(): Double {
    return DiscountAmount ?: 0.0
  }

  fun getPayablePrice(): Double {
    return if (getActualPrice() >= getDiscountAmount()) getActualPrice() - getDiscountAmount() else getActualPrice()
  }

  fun getPayablePWithCount():Double{
    return getPayablePrice() * productQuantityAdded
  }
}