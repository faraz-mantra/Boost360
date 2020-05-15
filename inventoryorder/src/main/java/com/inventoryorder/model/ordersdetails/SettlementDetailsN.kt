package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class SettlementDetailsN(
    val ExtraProperties: ExtraPropertiesN? = null,
    val ReferenceId: String? = null,
    val Status: String? = null,
    val TransferDate: String? = null
) : Serializable