package com.inventoryorder.rest.response.order

import com.framework.base.BaseResponse
import com.inventoryorder.model.product.Product

data class ProductResponse(
    val FloatingPoint: Any? = null,
    val Product: Product? = null,
    val ProductRating: Double? = null,
    val RecommendedProducts: List<Any>? = null,
    val isAPIRequest: Boolean? = null
) : BaseResponse()