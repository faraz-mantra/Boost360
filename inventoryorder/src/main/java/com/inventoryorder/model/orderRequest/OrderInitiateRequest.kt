package com.inventoryorder.model.orderRequest

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderInitiateRequest(
  @SerializedName("Mode")
  var mode: String = "",
  @SerializedName("ShippingDetails")
  var shippingDetails: ShippingDetails? = null,
  @SerializedName("SellerID")
  var sellerID: String? = null,
  @SerializedName("PaymentDetails")
  var paymentDetails: PaymentDetails? = null,
  @SerializedName("Items")
  var items: ArrayList<ItemsItem>? = null,
  @SerializedName("TransactionCharges")
  var transactionCharges: Double = 0.0,
  @SerializedName("GstCharges")
  var gstCharges: Double = 0.0,
  @SerializedName("BuyerDetails")
  var buyerDetails: BuyerDetails? = null,
  //for update
  @SerializedName("OrderId")
  var orderId: String? = null,
  @SerializedName("Status")
  var status: String? = null,
) : BaseRequest(), Serializable {
  var isVideoConsult: Boolean = false

}