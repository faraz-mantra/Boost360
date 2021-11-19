package com.festive.poster.models

import com.google.gson.annotations.SerializedName

data class PosterTagModel(
    @SerializedName("count")
    val Count: Int,
    @SerializedName("tag")
    val Tag: String
)