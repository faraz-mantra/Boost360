package com.boost.dbcenterapi.data.api_model

data class SomethingWentWrong(
    val errorCode: Int,
    val errorMessage: String,
    val correlationId: String
)