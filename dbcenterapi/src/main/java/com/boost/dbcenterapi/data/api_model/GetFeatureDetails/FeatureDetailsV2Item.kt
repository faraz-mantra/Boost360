package com.boost.dbcenterapi.data.api_model.GetFeatureDetails

data class FeatureDetailsV2Item(
    val activatedDate: String,
    val createdDate: String,
    val expiryDate: String,
    val featureCode: String,
    val featureKey: String,
    val featureState: Int,
    val featureType: Int,
    val properties: List<Property>
)