package com.appservice.model.serviceProduct

import com.google.gson.annotations.SerializedName

data class CreateProductRequest(

    @field:SerializedName("isAvailable")
    var isAvailable: Boolean? = null,

    @field:SerializedName("keySpecification")
    var keySpecification: KeySpecification? = null,

    @field:SerializedName("IsFreeShipmentAvailable")
    var isFreeShipmentAvailable: Boolean? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("discountAmount")
    var discountAmount: Double? = null,

    @field:SerializedName("variants")
    var variants: Boolean? = null,

    @field:SerializedName("buyOnlineLink")
    var buyOnlineLink: Any? = null,

    @field:SerializedName("identifierType")
    var identifierType: String? = null,

    @field:SerializedName("maxCodOrders")
    var maxCodOrders: Int? = null,

    @field:SerializedName("shipmentDuration")
    var shipmentDuration: Int? = null,

    @field:SerializedName("paymentType")
    var paymentType: String? = null,

    @field:SerializedName("pickupAddressReferenceId")
    var pickupAddressReferenceId: String? = null,

    @field:SerializedName("price")
    var price: Double? = null,

    @field:SerializedName("productType")
    var productType: String? = null,

    @field:SerializedName("fpTag")
    var fpTag: String? = null,

    @field:SerializedName("availableUnits")
    var availableUnits: Int? = null,

    @field:SerializedName("brandName")
    var brandName: String? = null,

    @field:SerializedName("clientId")
    var clientId: String? = null,

    @field:SerializedName("otherSpecifications")
    var otherSpecifications: List<OtherSpecificationsItem?>? = null,

    @field:SerializedName("isCodAvailable")
    var isCodAvailable: Boolean? = null,

    @field:SerializedName("uniquePaymentUrl")
    var uniquePaymentUrl: UniquePaymentUrl? = null,

    @field:SerializedName("priority")
    var priority: String? = null,

    @field:SerializedName("isPrepaidOnlineAvailable")
    var isPrepaidOnlineAvailable: Boolean? = null,

    @field:SerializedName("parentId")
    var parentId: Any? = null,

    @field:SerializedName("tags")
    var tags: List<String?>? = null,

    @field:SerializedName("maxPrepaidOnlineOrders")
    var maxPrepaidOnlineOrders: Int? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("category")
    var category: String? = null,

    @field:SerializedName("currencyCode")
    var currencyCode: String? = null,
)

data class UniquePaymentUrl(

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("url")
    var url: String? = null,
)

data class OtherSpecificationsItem(

    @field:SerializedName("value")
    var value: String? = null,

    @field:SerializedName("key")
    var key: String? = null,
)

data class KeySpecification(

    @field:SerializedName("value")
    var value: String? = null,

    @field:SerializedName("key")
    var key: String? = null,
)
