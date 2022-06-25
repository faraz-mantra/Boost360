package com.appservice.model.servicev1

import com.appservice.model.KeySpecification
import com.appservice.model.serviceProduct.UniquePaymentUrlN
import com.appservice.model.serviceTiming.ServiceTiming
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class ServiceModelV1(
  @SerializedName(value = "Name", alternate = ["name"])
  var Name: String? = null,
  @SerializedName(value = "Currency", alternate = ["CurrencyCode"])
  var CurrencyCode: String? = "INR",

  @SerializedName(value = "Description", alternate = ["description"])
  var Description: String? = null,

  @SerializedName(value = "Discountamount", alternate = ["DiscountAmount"])
  var DiscountAmount: Double = 0.0,
  var ExternalSourceId: String? = null,
  var IsArchived: String? = null,

  @SerializedName(value = "isAvailable", alternate = ["IsAvailable"])
  var IsAvailable: Boolean = true,


  @SerializedName(value = "Price", alternate = ["price"])
  var Price: Double = 0.0,

  @SerializedName(value = "priority", alternate = ["Priority"])
  var Priority: String? = null,
  var ShipmentDuration: String? = null,
  @SerializedName(value = "Duration", alternate = ["duration"])
  var Duration: Int? = null,

  @SerializedName(value = "GstSlab", alternate = ["gstSlab"])
  var GstSlab: Int=0,

  var availableUnits: Int = 1,
  @SerializedName(value = "tags", alternate = ["Tags"])
  var tags: ArrayList<String>? = null,
  var ApplicationId: String? = null,

  @SerializedName(value = "FloatingPointTag", alternate = ["FPTag"])
  var FPTag: String? = null,

  @SerializedName(value = "clientId", alternate = ["ClientId"])
  var ClientId: String? = null,

  @SerializedName("ServiceId", alternate = ["_id"])
  var productId: String? = null,
  var CreatedOn: String? = null,
  var UpdatedOn: String? = null,
  @SerializedName("serviceType", alternate = ["ServiceType"])
  var serviceType: List<Int> = arrayListOf(0),
  @SerializedName("BrandName", alternate = ["brandName"])
  var brandName: String? = null,
  @SerializedName("Category", alternate = ["category"])
  var category: String? = null,

  @SerializedName("isCodAvailable", alternate = ["IsCodAvailable"])
  var codAvailable: Boolean = false,

  @SerializedName("maxCodOrders")
  var maxCodOrders: Int = 10,

  @SerializedName("isPrepaidOnlineAvailable", alternate = ["IsPrepaidOnlineAvailable"])
  var prepaidOnlineAvailable: Boolean = true,

  @SerializedName("isPriceInclusiveOfTax", alternate = ["IsPriceInclusiveOfTax"])
  var isPriceInclusiveOfTax: Boolean = true,

  @SerializedName("url", alternate = ["Url", "uniquePaymentUrl"])
  var BuyOnlineLink: UniquePaymentUrlN? = null,
  @SerializedName("keySpecifications", alternate = ["KeySpecifications"])
  var keySpecification: KeySpecification? = null,

  @SerializedName("otherSpecifications", alternate = ["OtherSpecifications"])
  var otherSpecification: ArrayList<KeySpecification>? = null,

  @SerializedName("image", alternate = ["Image"])
  var image: ImageModel? = null,

  @SerializedName("secondaryImages", alternate = ["SecondaryImages"])
  var secondaryImages: ArrayList<ImageModel>? = null,

  @SerializedName("Timings")
  var timings: ArrayList<ServiceTiming>? = null,
  var pickupAddressReferenceId: String? = null,
) : Serializable {

  fun isPriceToggleOn(): Boolean {
    return this.Price > 0
  }

  enum class PaymentType(val value: String) {
    ASSURED_PURCHASE("AssuredPurchase"), MY_PAYMENT_GATEWAY("MyPaymentGateWay"), UNIQUE_PAYMENT_URL(
      "UniquePaymentUrl"
    ),
    DONT_WANT_TO_SELL("None");

    companion object {
      fun fromValue(value: String): PaymentType? =
        values().firstOrNull { it.value.equals(value, ignoreCase = true) }
    }
  }
}

data class ImageModel(
  var ImageId: String?,
  var ActualImage: String?,
  var TileImage: String?,
) : Serializable {

}