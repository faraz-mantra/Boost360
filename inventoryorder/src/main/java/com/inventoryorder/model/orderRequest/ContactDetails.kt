package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ContactDetails(
    @SerializedName("EmailId")
    val emailId: String = "",
    @SerializedName("FullName")
    val fullName: String = "",
    @SerializedName("PrimaryContactNumber")
    val primaryContactNumber: String = ""
) : Serializable