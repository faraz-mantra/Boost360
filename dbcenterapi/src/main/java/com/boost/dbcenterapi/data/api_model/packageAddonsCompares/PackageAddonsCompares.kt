package com.boost.dbcenterapi.data.api_model.packageAddonsCompares

data class PackageAddonsCompares(
    var packsAvailableIn: ArrayList<AddonsPacksIn>,
    val featureCode: String,
    val title: String
)