package com.nowfloats.education.model

import com.nowfloats.education.batches.model.Data

data class UpcomingBatchesResponse(
        val Data: List<Data>,
        val Extra: Extra
)