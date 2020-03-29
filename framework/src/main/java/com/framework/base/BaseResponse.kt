package com.framework.base

open class BaseResponse(
        var status: Int? = null,
        var message: String? = null,
        var error: Throwable? = null,
        var stringResponse: String? = null,
        var arrayResponse: Array<*>? = null
)