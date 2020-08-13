package com.appservice.model.serviceProduct

import android.net.Uri
import com.appservice.model.KeySpecification
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Product(
    @SerializedName(value = "currencycode", alternate = ["CurrencyCode"])
    var CurrencyCode: String? = null,

    @SerializedName(value = "description", alternate = ["Description"])
    var Description: String? = null,

    @SerializedName(value = "discountAmount", alternate = ["DiscountAmount"])
    var DiscountAmount: Double = 0.0,
    var ExternalSourceId: String? = null,
    var IsArchived: String? = null,

    @SerializedName(value = "isAvailable", alternate = ["IsAvailable"])
    var IsAvailable: Boolean = false,

    @SerializedName(value = "isFreeShipmentAvailable", alternate = ["IsFreeShipmentAvailable"])
    var IsFreeShipmentAvailable: String? = null,

    @SerializedName(value = "name", alternate = ["Name"])
    var Name: String? = null,

    @SerializedName(value = "price", alternate = ["Price"])
    var Price: Double = 0.0,

    @SerializedName(value = "priority", alternate = ["Priority"])
    var Priority: String? = null,
    var ShipmentDuration: String? = null,
    var availableUnits: Int = 1,
    var _keywords: ArrayList<String>? = null,
    var tags: ArrayList<String>? = null,
    var ApplicationId: String? = null,

    @SerializedName(value = "fpTag", alternate = ["FPTag"])
    var FPTag: String? = null,

    @SerializedName(value = "clientId", alternate = ["ClientId"])
    var ClientId: String? = null,
    var ImageUri: String? = null,
    var ProductUrl: String? = null,
    var Images: ArrayList<ImageListModel>? = null,
    var MerchantName: String? = null,
    var TileImageUri: String? = null,

    @SerializedName("_id")
    var productId: String? = null,
    var GPId: String? = null,
    var TotalQueries: String? = null,
    var CreatedOn: String? = null,
    var ProductIndex: String? = null,
    var picimageURI: Uri? = null,
    var UpdatedOn: String? = null,
    var isProductSelected: Boolean = false,
    var productType: String? = null,
    var paymentType: String? = null,
    var variants: Boolean = false,
    var brandName: String? = null,
    var category: String? = null,

    @SerializedName("isCodAvailable")
    var codAvailable: Boolean = false,

    @SerializedName("maxCodOrders")
    var maxCodOrders: Int = 10,

    @SerializedName("isPrepaidOnlineAvailable")
    var prepaidOnlineAvailable: Boolean = true,

    @SerializedName("maxPrepaidOnlineOrders")
    var maxPrepaidOnlineAvailable: Int = 10,

    @SerializedName("uniquePaymentUrl")
    var BuyOnlineLink: BuyOnlineLink? = null,
    var keySpecification: KeySpecification? = null,

    @SerializedName("otherSpecifications")
    var otherSpecification: ArrayList<KeySpecification>? = null,
    var pickupAddressReferenceId: String? = null
) : Serializable {

  enum class PaymentType(val value: String) {
    ASSURED_PURCHASE("AssuredPurchase"), MY_PAYMENT_GATEWAY("MyPaymentGateWay"), UNIQUE_PAYMENT_URL("UniquePaymentUrl"), DONT_WANT_TO_SELL("None");

    companion object {
      fun fromValue(value: String): PaymentType? = values().firstOrNull { it.value.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }
}