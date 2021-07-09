package com.inventoryorder.model

import com.framework.base.BaseResponse

data class OrderConfirmStatus(
    var Status: String? = null,
    var Message: Any? = null
) : BaseResponse()