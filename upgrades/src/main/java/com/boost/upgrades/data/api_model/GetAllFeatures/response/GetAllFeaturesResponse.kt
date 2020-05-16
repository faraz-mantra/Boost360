package com.boost.upgrades.data.api_model.GetAllFeatures.response

import com.google.gson.annotations.SerializedName

data class GetAllFeaturesResponse(
        val Data: List<Data>,
        val Extra: Extra
)