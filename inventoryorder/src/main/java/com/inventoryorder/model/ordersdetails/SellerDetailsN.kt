package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class SellerDetailsN(
    val Address: AddressN? = null,
    val ContactDetails: ContactDetailsN? = null,
    val Identifier: String? = null
) : Serializable