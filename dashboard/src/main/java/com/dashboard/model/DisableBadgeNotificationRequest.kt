package com.dashboard.model

import com.google.gson.annotations.SerializedName

data class DisableBadgeNotificationRequest(
  @field:SerializedName("fptag")
  var fpTag: String? = null,

  @field:SerializedName("notificationtype")
  var notificationType: String? = null,

  @field:SerializedName("flagid")
  var flagId: String? = null
)