package com.boost.dbcenterapi.data.api_model.mycurrentPlanV3

data class FeatureDetails(
    val ActivatedDate: String,
    val CreatedDate: String,
    val ExpiryDate: String,
    val FeatureKey: String,
    val FeatureState: Int,
    val FeatureType: Int,
    val Properties: List<Property>
)