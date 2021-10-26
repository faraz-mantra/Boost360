package com.festive.poster.models

import com.google.gson.annotations.SerializedName

data class PosterPackTagModel(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tag")
    val tag: String
)