package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class CancellationDetailsN(
  val CancelledBy: String? = null,
  val CancelledOn: String? = null,
  val ExtraProperties: ExtraPropertiesN? = null
) : Serializable {

  fun cancelledBy(): String {
    return CancelledBy ?: ""
  }
}