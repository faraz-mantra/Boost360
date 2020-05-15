package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class ExtraDeliveryInformationN(
    val additionalProp1: AdditionalPropN? = null,
    val additionalProp2: AdditionalPropN? = null,
    val additionalProp3: AdditionalPropN? = null
) : Serializable