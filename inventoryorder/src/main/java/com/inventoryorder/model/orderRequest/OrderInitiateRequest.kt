package com.inventoryorder.model.orderRequest

import com.framework.base.BaseRequest
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderInitiateRequest(
    @SerializedName("Mode")
    var mode: String = "",
    @SerializedName("ShippingDetails")
    var shippingDetails: ShippingDetails? = null,
    @SerializedName("SellerID")
    var sellerID: String = "",
    @SerializedName("PaymentDetails")
    var paymentDetails: PaymentDetails? = null,
    @SerializedName("Items")
    var items: List<ItemsItem>? = null,
    @SerializedName("TransactionCharges")
    var transactionCharges: Double = 0.0,
    @SerializedName("GstCharges")
    var gstCharges: Double = 0.0,
    @SerializedName("BuyerDetails")
    var buyerDetails: BuyerDetails? = null
) : BaseRequest(), Serializable {
  var isVideoConsult: Boolean = false
}