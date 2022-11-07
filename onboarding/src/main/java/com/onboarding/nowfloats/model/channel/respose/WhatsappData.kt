package com.onboarding.nowfloats.model.channel.respose

import java.io.Serializable

data class WhatsappData(
  val ActionId: String? = null,
  val CreatedOn: String? = null,
  val IsArchived: Boolean? = null,
  val UpdatedOn: String? = null,
  val UserId: String? = null,
  val WebsiteId: String? = null,
  val _id: String? = null,
  val active_whatsapp_number: String? = null
) : Serializable {

  fun getNumberPlus91(): String? {
    return when {
      active_whatsapp_number?.contains("+91-") == true -> active_whatsapp_number.replace("+91-", "+91")
      active_whatsapp_number?.contains("+91") == false -> "+91$active_whatsapp_number"
      else -> active_whatsapp_number
    }
  }
}