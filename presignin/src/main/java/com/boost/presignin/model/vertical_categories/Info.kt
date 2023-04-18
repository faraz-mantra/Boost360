package com.boost.presignin.model.vertical_categories

data class Info(
    val _id: String,
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val createdon: String,
    val desc: Any,
    val icon: String,
    val isarchived: Boolean,
    val updatedon: String,
    val websiteid: String
)