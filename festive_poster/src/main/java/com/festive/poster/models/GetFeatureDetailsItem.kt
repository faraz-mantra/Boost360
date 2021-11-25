package com.festive.poster.models

data class GetFeatureDetailsItem(
    val activatedDate: String,
    val createdDate: String,
    val expiryDate: String,
    val featureKey: String,
    val featureState: Int,
    val featureType: Int,
    val properties: List<Any>
)