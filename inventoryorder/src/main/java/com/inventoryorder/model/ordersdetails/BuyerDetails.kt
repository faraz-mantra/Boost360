package com.inventoryorder.model.ordersdetails

data class BuyerDetails(
    val Address: Address? = null,
    val ContactDetails: ContactDetails? = null,
    val ExtraProperties: ExtraProperties? = null
)