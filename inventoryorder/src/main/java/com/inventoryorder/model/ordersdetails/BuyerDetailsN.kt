package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class BuyerDetailsN(
  val Address: AddressN? = null,
  val ContactDetails: ContactDetailsN? = null,
  val ExtraProperties: Any? = null,
  val GSTIN: Any? = null,
) : Serializable {

  fun getAddressFull(): String? {
    if (ContactDetails?.EmailId.isNullOrEmpty()) {
      return "${ContactDetails?.PrimaryContactNumber?.trim()}\n${address().addressLine1().trim()}"
    }
    return "${ContactDetails?.PrimaryContactNumber?.trim()}, ${ContactDetails?.EmailId?.trim()}\n${
      address().addressLine1().trim()
    }"
  }

  fun getPhoneEmailFull(): String? {
    if (ContactDetails?.EmailId.isNullOrEmpty() || ContactDetails?.PrimaryContactNumber.isNullOrEmpty()) return "${ContactDetails?.PrimaryContactNumber?.trim()}${ContactDetails?.EmailId?.trim()}"
    return "${ContactDetails?.PrimaryContactNumber?.trim()}\n${ContactDetails?.EmailId?.trim()}"
  }

  fun address(): AddressN {
    return Address ?: AddressN()
  }

  fun getFullAddressDetail(): String {
    val address = StringBuilder()
    Address?.let {
      if (it.AddressLine1.isNullOrBlank().not()) address.append(it.AddressLine1 ?: "")
      if (it.AddressLine2.isNullOrBlank().not()) address.append(", ${it.AddressLine2 ?: ""}")
      if (it.City.isNullOrBlank().not()) address.append(", ${it.City ?: ""} ")
      if (it.Region.isNullOrBlank().not()) address.append(", ${it.Region ?: ""} ")
      if (it.Zipcode.isNullOrBlank().not()) address.append(" - ${it.Zipcode ?: ""} ")
    }
    return address.toString()
  }
}