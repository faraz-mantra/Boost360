package com.boost.upgrades.data.api_model.GetAllFeatures.response

data class Data(
    val _kid: String,
    val createdon: String,
    val feature_deals: List<Any>,
    val features: List<Feature>,
    val isarchived: Boolean,
    val rootaliasurl: Rootaliasurl,
    val schemaid: String,
    val updatedon: String,
    val userid: String,
    val websiteid: String
)