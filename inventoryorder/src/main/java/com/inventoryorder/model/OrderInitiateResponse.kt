package com.inventoryorder.model

import com.google.gson.annotations.SerializedName
import com.inventoryorder.model.ordersdetails.OrderItem

data class OrderInitiateResponse(@SerializedName("Status")
                                 val status: String = "",
                                 @SerializedName("Message")
                                 val message: String = "",
                                 @SerializedName("Data")
                                 val data: OrderItem)