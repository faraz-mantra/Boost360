package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class ExtraPropertiesN(
    val additionalProp1: AdditionalPropN,
    val additionalProp2: AdditionalPropN,
    val additionalProp3: AdditionalPropN
) : Serializable