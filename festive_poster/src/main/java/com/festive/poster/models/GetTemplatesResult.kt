package com.festive.poster.models

import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterTagModel
import com.google.gson.annotations.SerializedName

data class GetTemplatesResult(
    @SerializedName("tags")
    val tags: List<PosterTagModel>?,
    @SerializedName("templates")
    val templates: List<PosterModel>?,
    @SerializedName("totalCount")
    val totalCount: Int?
)