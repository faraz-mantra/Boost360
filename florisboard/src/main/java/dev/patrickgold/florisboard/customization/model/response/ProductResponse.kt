package dev.patrickgold.florisboard.customization.model.response

import java.io.Serializable

class ProductResponse(
    val Products:List<Product>?=null,
    val TotalCount:Int?=null
):Serializable