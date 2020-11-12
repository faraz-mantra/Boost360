package com.inventoryorder.model.floatMessage

import java.io.Serializable

data class FloatsMessageModel(
    var _id: String? = null,
    var createdOn: String? = null,
    var imageUri: String? = null,
    var message: String? = null,
    var tileImageUri: String? = null,
    var type: String? = null,
    var url: String? = null,
) : Serializable