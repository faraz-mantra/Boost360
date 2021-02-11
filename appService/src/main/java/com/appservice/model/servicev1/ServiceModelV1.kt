package com.appservice.model.servicev1

import com.appservice.model.KeySpecification
import com.appservice.model.serviceProduct.BuyOnlineLink
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
        var IsAvailable: Boolean = false,


        @SerializedName(value = "Price", alternate = ["price"])
        var Price: Double = 0.0,

        @SerializedName(value = "priority", alternate = ["Priority"])
        var Priority: String? = null,
        var ShipmentDuration: String? = null,
        @SerializedName(value = "Duration", alternate = ["duration"])
        var Duration: Int? = null,

        @SerializedName(value = "GstSlab", alternate = ["gstSlab"])
        var GstSlab: Int? = null,

        var availableUnits: Int = 1,

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
        var isProductSelected: Boolean = false,
        var productType: String? = null,
        var serviceType: List<Int> = arrayListOf(0),
        var paymentType: String? = null,
        var variants: Boolean = false,
        @SerializedName("BrandName", alternate = ["brandName"])
        var brandName: String? = null,
        @SerializedName("Category", alternate = ["category"])
        var category: String? = null,

        @SerializedName("isCodAvailable")
        var codAvailable: Boolean = false,

        @SerializedName("maxCodOrders")
        var maxCodOrders: Int = 10,

        @SerializedName("isPrepaidOnlineAvailable")
        var prepaidOnlineAvailable: Boolean = true,

        @SerializedName("maxPrepaidOnlineOrders")
        var maxPrepaidOnlineAvailable: Int = 10,

        @SerializedName("url",alternate = ["uniquePaymentUrl"])
        var BuyOnlineLink: BuyOnlineLink? = null,
        @SerializedName("KeySpecifications")
        var keySpecification: KeySpecification? = null,

        @SerializedName("com.appservice.ui.model.OtherSpecifications")
        var otherSpecification: ArrayList<KeySpecification>? = null,

        @SerializedName("Image")
        var image: ImageModel? = null,

        @SerializedName("SecondaryImages")
        var secondaryImages: ArrayList<ImageModel>? = null,

        var pickupAddressReferenceId: String? = null,
) : Serializable {

    fun isPriceToggleOn(): Boolean {
        return this.Price > 0
    }

    enum class PaymentType(val value: String) {
        ASSURED_PURCHASE("AssuredPurchase"), MY_PAYMENT_GATEWAY("MyPaymentGateWay"), UNIQUE_PAYMENT_URL("UniquePaymentUrl"), DONT_WANT_TO_SELL("None");

        companion object {
            fun fromValue(value: String): PaymentType? = values().firstOrNull { it.value.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
        }
    }
}

data class ImageModel(
        var ImageId: String?,
        var ActualImage: String?,
        var TileImage: String?
) : Serializable {

}