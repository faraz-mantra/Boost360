package com.boost.presignin.model.activatepurchase


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PurchasedWidget(
  @SerializedName("ConsumptionConstraint")
  var consumptionConstraint: ConsumptionConstraint? = null,
  @SerializedName("Desc")
  var desc: String? = null,
  @SerializedName("Discount")
  var discount: Double? = null,
  @SerializedName("Expiry")
  var expiry: PurchasedExpiry? = null,
  @SerializedName("Images")
  var images: List<String>? = null,
  @SerializedName("IsCancellable")
  var isCancellable: Boolean? = null,
  @SerializedName("IsRecurringPayment")
  var isRecurringPayment: Boolean? = null,
  @SerializedName("Name")
  var name: String? = null,
  @SerializedName("NetPrice")
  var netPrice: Double? = null,
  @SerializedName("Price")
  var price: Double? = null,
  @SerializedName("Quantity")
  var quantity: Int? = null,
  @SerializedName("RecurringPaymentFrequency")
  var recurringPaymentFrequency: String? = null,
  @SerializedName("WidgetKey")
  var widgetKey: String? = null
) : Serializable