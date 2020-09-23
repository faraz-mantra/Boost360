package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class BuyerDetailsN(
    val Address: AddressN? = null,
    val ContactDetails: ContactDetailsN? = null,
    val ExtraProperties: ExtraPropertiesN? = null
) : Serializable {

  fun getAddressFull(): String? {
    if(ContactDetails?.EmailId?.trim().isNullOrEmpty()){
      return "${ContactDetails?.PrimaryContactNumber?.trim()}\n${address().addressLine1().trim()}"
    }
    return "${ContactDetails?.PrimaryContactNumber?.trim()}, ${ContactDetails?.EmailId?.trim()}\n${address().addressLine1().trim()}"
  }

  fun address(): AddressN {
    return Address ?: AddressN()
  }
}