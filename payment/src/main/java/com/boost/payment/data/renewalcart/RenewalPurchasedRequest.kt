package com.boost.payment.data.renewalcart

import java.io.Serializable

data class RenewalPurchasedRequest(
  var floatingPointId: String? = null,
  var clientId: String? = null,
  var widgetStatus: String? = null,
  var nextWidgetStatus: String? = null,
  var dateFilter: String? = null,
  var startDate: String? = null,
  var endDate: String? = null,
  var widgetKey: String? = null
) : Serializable {

  enum class WidgetStatus {
    ACTIVE, EXPIRED, CANCELLED, TO_BE_ACTIVATED, PENDING
  }

  enum class NextWidgetStatus {
    DEACTIVATION, PENDING_ACTIVATION, RENEWAL, ACTIVATION
  }

  enum class DateFilter {
    EXPIRY_DATE, ACTIVATION_DATE, CREATION_DATE
  }
}