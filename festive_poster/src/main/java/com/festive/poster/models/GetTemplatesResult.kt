package com.festive.poster.models

import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterTagModel

data class GetTemplatesResult(
    val Tags: List<PosterTagModel>,
    val Templates: List<PosterModel>,
    val TotalCount: Int
)