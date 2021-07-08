package com.appservice.model.serviceProduct.informationFetch


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataInfo(
  @SerializedName("Addresses")
  var addresses: Any? = null,
  @SerializedName("BankAccount")
  var bankAccount: BankAccountInfo? = null,
  @SerializedName("BoostSubscription")
  var boostSubscription: Any? = null,
  @SerializedName("CreatedOn")
  var createdOn: String? = null,
  @SerializedName("DeliveryMethod")
  var deliveryMethod: String? = null,
  @SerializedName("ESIGN")
  var eSIGN: Any? = null,
  @SerializedName("GSTN")
  var gSTN: String? = null,
  @SerializedName("GstSlab")
  var gstSlab: Any? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("IsArchived")
  var isArchived: Boolean? = null,
  @SerializedName("OptIn")
  var optIn: Boolean? = null,
  @SerializedName("PaymentMethod")
  var paymentMethod: Any? = null,
  @SerializedName("SellOnline")
  var sellOnline: Boolean? = null,
  @SerializedName("SellerAddressId")
  var sellerAddressId: Any? = null,
  @SerializedName("SellerId")
  var sellerId: String? = null,
  @SerializedName("ShippingCost")
  var shippingCost: Any? = null,
  @SerializedName("SiteAppearance")
  var siteAppearance: Any? = null,
  @SerializedName("TnC")
  var tnC: Boolean? = null,
  @SerializedName("UpdatedOn")
  var updatedOn: String? = null
) : Serializable