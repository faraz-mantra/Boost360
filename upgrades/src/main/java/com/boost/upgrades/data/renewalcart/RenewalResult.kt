package com.boost.upgrades.data.renewalcart


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RenewalResult(
    @SerializedName("ActivationDate")
    var activationDate: String? = null,
    @SerializedName("Category")
    var category: String? = null,
    @SerializedName("ConsumptionConstraint")
    var consumptionConstraint: ConsumptionConstraint? = null,
    @SerializedName("DependentWidget")
    var dependentWidget: Any? = null,
    @SerializedName("Desc")
    var desc: String? = null,
    @SerializedName("Discount")
    var discount: Double? = null,
    @SerializedName("Expiry")
    var expiry: Expiry? = null,
    @SerializedName("ExpiryDate")
    var expiryDate: String? = null,
    @SerializedName("Images")
    var images: List<String>? = null,
    @SerializedName("IsRecurringPayment")
    var isRecurringPayment: Boolean? = null,
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("NetPrice")
    var netPrice: Double? = null,
    @SerializedName("NextWidgetStatus")
    var nextWidgetStatus: String? = null,
    @SerializedName("Price")
    var price: Double? = null,
    @SerializedName("Properties")
    var properties: Any? = null,
    @SerializedName("Quantity")
    var quantity: Int? = null,
    @SerializedName("RecurringPaymentFrequency")
    var recurringPaymentFrequency: String? = null,
    @SerializedName("RenewalStatus")
    var renewalStatus: String? = null,
    @SerializedName("WidgetId")
    var widgetId: String? = null,
    @SerializedName("WidgetKey")
    var widgetKey: String? = null,
    @SerializedName("WidgetStatus")
    var widgetStatus: String? = null
) : Serializable {

  fun renewalStatus(): String {
    return renewalStatus ?: ""
  }

  fun nextWidgetStatus(): String {
    return nextWidgetStatus ?: ""
  }

  fun widgetStatus(): String {
    return widgetStatus ?: ""
  }

  enum class RenewalStatus {
    PENDING
  }
}