package dev.patrickgold.florisboard.customization.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Updates(
    @SerializedName("floats")
    @Expose
    var floats: List<Float?>? = null,

    @SerializedName("moreFloatsAvailable")
    @Expose
    var moreFloatsAvailable: Boolean? = null,

    @SerializedName("totalCount")
    @Expose
    var totalCount: Int? = null,
)