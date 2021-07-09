package com.inventoryorder.model.spaAppointment

import java.io.Serializable

data class ServiceItem(
    var BrandName: Any ?= null,
    var Category: String ?= null,
    var Currency: String ?= null,
    var Description: String ?= null,
    var DiscountAmount: Double ?= null,
    var DiscountedPrice: Double ?= null,
    var Duration: Int ?= null,
    var Image: String ?= null,
    var Name: String ?= null,
    var Price: Double ?= null,
    var SecondaryImages: List<Any> ?= null,
    var SecondaryTileImages: List<Any> ?= null,
    var TileImage: String?= null,
    var Type: List<Int>?= null,
    var _id: String?= null
) : Serializable