package com.boost.dbcenterapi.data.api_model.GetAllFeatures.response

data class FeatureVideoLink(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val createdon: String,
    val isarchived: Boolean,
    val title: String,
    val updatedon: String,
    val websiteid: String,
    val youtube_link: String
)