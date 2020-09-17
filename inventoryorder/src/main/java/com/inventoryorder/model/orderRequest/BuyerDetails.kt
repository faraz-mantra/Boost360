package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName

data class BuyerDetails(@SerializedName("ContactDetails")
                        val contactDetails: ContactDetails,
                        @SerializedName("Address")
                        val address: Address)