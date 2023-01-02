package com.boost.dbcenterapi.data.api_model.vmn

data class PurchasedVmnResponse(
    val Vmn: String,
    val fpTag: String,
    val is_Active: Boolean,
    val is_Expired: Boolean
)