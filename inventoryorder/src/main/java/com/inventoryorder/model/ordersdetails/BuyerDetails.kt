package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class BuyerDetails(
    val Address: Address? = null,
    val ContactDetails: ContactDetails? = null,
    val ExtraProperties: ExtraProperties? = null
) : Serializable