package com.inventoryorder.constant

import com.framework.BuildConfig

class AppConstant {
  companion object {
    const val CLIENT_ID_ORDER = BuildConfig.CLIENT_ID_ORDER

    const val TYPE_APPOINTMENT = "appointment"
    const val GST_VALIDATION_REGEX = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}\$"
    const val GST_PERCENTAGE = 1.18
  }
}