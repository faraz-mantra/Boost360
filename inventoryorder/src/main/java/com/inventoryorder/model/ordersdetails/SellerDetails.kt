package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class SellerDetails(
    val Address: Address? = null,
    val ContactDetails: ContactDetails? = null,
    val Identifier: String? = null
) : Serializable