package com.appservice.model


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceCreateRequest(
    @SerializedName("availableUnits")
    var availableUnits: Int? = null,
    @SerializedName("brandName")
    var brandName: String? = null,
    @SerializedName("buyOnlineLink")
    var buyOnlineLink: String? = null,
    @SerializedName("category")
    var category: String? = null,
    @SerializedName("clientId")
    var clientId: String? = null,
    @SerializedName("currencyCode")
    var currencyCode: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("discountAmount")
    var discountAmount: Int? = null,
    @SerializedName("ExternalSourceId")
    var externalSourceId: String? = null,
    @SerializedName("fpTag")
    var fpTag: String? = null,
    @SerializedName("identifierType")
    var identifierType: String? = null,
    @SerializedName("isAvailable")
    var isAvailable: Boolean? = null,
    @SerializedName("isCodAvailable")
    var isCodAvailable: Boolean? = null,
    @SerializedName("isFreeShipmentAvailable")
    var isFreeShipmentAvailable: Boolean? = null,
    @SerializedName("isPrepaidOnlineAvailable")
    var isPrepaidOnlineAvailable: Boolean? = null,
    @SerializedName("keySpecification")
    var keySpecification: KeySpecification? = null,
    @SerializedName("_keywords")
    var keywords: List<String>? = null,
    @SerializedName("maxCodOrders")
    var maxCodOrders: Int? = null,
    @SerializedName("maxPrepaidOnlineOrders")
    var maxPrepaidOnlineOrders: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("otherSpecifications")
    var otherSpecifications: List<OtherSpecification>? = null,
    @SerializedName("parentId")
    var parentId: String? = null,
    @SerializedName("paymentType")
    var paymentType: Int? = null,
    @SerializedName("pickupAddressReferenceId")
    var pickupAddressReferenceId: String? = null,
    @SerializedName("price")
    var price: Int? = null,
    @SerializedName("priority")
    var priority: String? = null,
    @SerializedName("productType")
    var productType: Int? = null,
    @SerializedName("shipmentDuration")
    var shipmentDuration: Int? = null,
    @SerializedName("tags")
    var tags: List<String>? = null,
    @SerializedName("uniquePaymentUrl")
    var uniquePaymentUrl: UniquePaymentUrl? = null,
    @SerializedName("variants")
    var variants: Boolean? = null
) : BaseRequest(), Serializable