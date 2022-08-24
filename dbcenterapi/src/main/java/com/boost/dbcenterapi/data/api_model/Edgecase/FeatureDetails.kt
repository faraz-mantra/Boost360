package com.boost.dbcenterapi.data.api_model.Edgecase

data class FeatureDetails(
    val ActivatedDate: String,
    val CreatedDate: String,
    val ExpiryDate: String,
    val ExtraDetails: Any,
    val FeatureKey: String,
    val FeatureState: Int,
    val FeatureType: Int,
    val IsAutoRenewalEnabled: Boolean,
    val Properties: List<Any>,
    val RenewalFrequency: String
)