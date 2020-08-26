package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName

data class Address(@SerializedName("AddressLine1")
                   val addressLine: Any? = null)