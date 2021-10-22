package com.festive.poster.models

import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterTagModel

data class GetTemplatesResult(
    val tags: List<PosterTagModel>,
    val templates: List<PosterModel>,
    val totalCount: Int
)