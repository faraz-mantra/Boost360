package com.onboarding.nowfloats.model.riaWhatsapp

import com.framework.base.BaseRequest

data class RiaWhatsappRequest(
  val client_id: String? = null,
  val optType: String? = null,
  //TODO number with country code
  val whatsappNumber: String? = null,
  val notificationType: String? = null,
  val customerId: String? = null,
  //TODO Optional default value "919381915059"
  val optinId: String? = null,
) : BaseRequest() {

  enum class OptType {
    OPTIN, OPTOUT
  }

  enum class NotificationType {
    RIANotificationType, SubscriberNotificationType, UserMessageNotificationType, ReportsNotificationType, SearchTrafficNotificationType
  }
}