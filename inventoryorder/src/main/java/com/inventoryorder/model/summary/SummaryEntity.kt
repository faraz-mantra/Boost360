package com.inventoryorder.model.summary


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SummaryEntity(
    @SerializedName("NoOfMessages")
    var noOfMessages: Int? = null,
    @SerializedName("NoOfSubscribers")
    var noOfSubscribers: Int? = null,
    @SerializedName("NoOfUniqueViews")
    var noOfUniqueViews: Int? = null,
    @SerializedName("NoOfViews")
    var noOfViews: Int? = null,
) : Serializable