package com.boost.presignin.model.other

import com.google.gson.annotations.SerializedName

data class KycExtra(
        @SerializedName("CurrentIndex")
        var currentIndex: Int? = null,
        @SerializedName("PageSize")
        var pageSize: Int? = null,
        @SerializedName("TotalCount")
        var totalCount: Int? = null
)