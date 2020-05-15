package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class BuyerDetails(
    val Address: Address? = null,
    val ContactDetails: ContactDetails? = null,
    val ExtraProperties: ExtraProperties? = null
) : Serializable {

  fun getAddressFull(): String? {
    return ContactDetails?.PrimaryContactNumber?.trim() + ", " + ContactDetails?.EmailId?.trim() + "\n" + Address?.AddressLine1?.trim()
  }

}