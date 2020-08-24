package com.appservice.model.serviceProduct

import java.io.Serializable

data class ImageListModel(
    var ImageUri: String? = null,
    var TileImageUri: String? = null
) : Serializable