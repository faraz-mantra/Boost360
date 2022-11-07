package com.framework.glide.customsvgloader

import com.google.gson.annotations.SerializedName

data class PosterKeyModel(
    @SerializedName("default")
    val default: String?,
    @SerializedName("length")
    val length: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("custom")
    var custom:String?,
)