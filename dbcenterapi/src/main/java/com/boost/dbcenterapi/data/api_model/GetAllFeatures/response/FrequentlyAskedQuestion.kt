package com.boost.dbcenterapi.data.api_model.GetAllFeatures.response

data class FrequentlyAskedQuestion(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val answer: String,
    val createdon: String,
    val isarchived: Boolean,
    val question: String,
    val updatedon: String,
    val websiteid: String
)