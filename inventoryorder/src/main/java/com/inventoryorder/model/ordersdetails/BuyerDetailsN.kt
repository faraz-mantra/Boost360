package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class BuyerDetailsN(
    val Address: AddressN? = null,
    val ContactDetails: ContactDetailsN? = null,
    val ExtraProperties: ExtraPropertiesN? = null,
) : Serializable {

  fun getAddressFull(): String? {
    if (ContactDetails?.EmailId.isNullOrEmpty()) {
      return "${ContactDetails?.PrimaryContactNumber?.trim()}\n${address().addressLine1().trim()}"
    }
    return "${ContactDetails?.PrimaryContactNumber?.trim()}, ${ContactDetails?.EmailId?.trim()}\n${address().addressLine1().trim()}"
  }

  fun getPhoneEmailFull(): String? {
    if (ContactDetails?.EmailId.isNullOrEmpty() || ContactDetails?.PrimaryContactNumber.isNullOrEmpty()) return "${ContactDetails?.PrimaryContactNumber?.trim()}${ContactDetails?.EmailId?.trim()}"
    return "${ContactDetails?.PrimaryContactNumber?.trim()}\n${ContactDetails?.EmailId?.trim()}"
  }

  fun address(): AddressN {
    return Address ?: AddressN()
  }
}