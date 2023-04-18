package com.boost.presignin.model.vertical_categories

data class CategoriesItem(
    val _id: String,
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val appexperiencecodes: List<String>,
    val createdon: String,
    val info: Info,
    val isarchived: Boolean,
    val name: String,
    val updatedon: String,
    val websiteid: String
)