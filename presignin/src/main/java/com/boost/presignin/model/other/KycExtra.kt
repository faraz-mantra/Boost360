package com.boost.presignin.model.other

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KycExtra(
    @SerializedName("CurrentIndex")
    var currentIndex: Int? = null,
    @SerializedName("PageSize")
    var pageSize: Int? = null,
    @SerializedName("TotalCount")
    var totalCount: Int? = null,
):Serializable