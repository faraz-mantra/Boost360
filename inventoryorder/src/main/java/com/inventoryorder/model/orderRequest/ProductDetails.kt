package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductDetails(
        @SerializedName("CurrencyCode")
        var currencyCode: String? = null,
        @SerializedName("Description")
        var description: String? = null,
        @SerializedName("DiscountAmount")
        var discountAmount: Double? = null,
        @SerializedName("ExtraProperties")
        var extraProperties: ExtraProperties? = null,
        @SerializedName("_id")
        var id: String? = null,
        @SerializedName("IsAvailable")
        var isAvailable: Boolean? = null,
        @SerializedName("Name")
        var name: String? = null,
        @SerializedName("Price")
        var price: Double? = null,
        @SerializedName("ShippingCost")
        var shippingCost: Double? = null,
        @SerializedName("ImageUri")
        var imageUri: String? = null
) : Serializable