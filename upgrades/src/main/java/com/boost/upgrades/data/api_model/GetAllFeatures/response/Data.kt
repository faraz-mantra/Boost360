package com.boost.upgrades.data.api_model.GetAllFeatures.response

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("_kid")
    val _kid: String,
    @SerializedName("createdon")
    val createdon: String,
    @SerializedName("feature_deals")
    val feature_deals: List<FeatureDeals>,
    @SerializedName("features")
    val features: List<Feature>,
    @SerializedName("bundles")
    val bundles: List<Bundles>,
    @SerializedName("isarchived")
    val isarchived: Boolean,
    @SerializedName("rootaliasurl")
    val rootaliasurl: Rootaliasurl,
    @SerializedName("schemaid")
    val schemaid: String,
    @SerializedName("updatedon")
    val updatedon: String,
    @SerializedName("userid")
    val userid: String,
    @SerializedName("websiteid")
    val websiteid: String
)