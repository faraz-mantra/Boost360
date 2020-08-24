package com.appservice.model.kycData


import com.google.gson.annotations.SerializedName

data class Extra(
    @SerializedName("CurrentIndex")
    var currentIndex: Int? = null,
    @SerializedName("PageSize")
    var pageSize: Int? = null,
    @SerializedName("TotalCount")
    var totalCount: Int? = null
)