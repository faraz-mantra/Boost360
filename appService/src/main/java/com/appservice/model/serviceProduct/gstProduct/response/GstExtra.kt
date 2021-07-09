package com.appservice.model.serviceProduct.gstProduct.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GstExtra(
    @SerializedName("CurrentIndex")
    var currentIndex: Int? = null,
    @SerializedName("PageSize")
    var pageSize: Int? = null,
    @SerializedName("TotalCount")
    var totalCount: Int? = null
) : Serializable