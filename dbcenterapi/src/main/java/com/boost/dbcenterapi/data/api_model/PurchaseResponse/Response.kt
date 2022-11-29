package com.boost.dbcenterapi.data.api_model.PurchaseResponse

data class Response(
    val Error: Error,
    val Result: List<Result>,
    val StatusCode: Int
)