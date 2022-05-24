package com.festive.poster.models

import com.google.gson.annotations.SerializedName

data class FestivePosterSectionModel(
    @SerializedName("description")
    val description: String?,
    @SerializedName("tags")
    val tags: List<PosterPackTagModel>?
)