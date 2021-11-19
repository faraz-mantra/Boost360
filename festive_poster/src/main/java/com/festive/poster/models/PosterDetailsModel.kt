package com.festive.poster.models

import com.google.gson.annotations.SerializedName

data class PosterDetailsModel(
    @SerializedName("description")
    val Description: String,
    @SerializedName("favourite")
    val Favourite: Boolean,
    @SerializedName("price")
    val Price: Double,
    @SerializedName("title")
    val Title: String,
    @SerializedName("isPurchased")
    val isPurchased: Boolean
)