package com.boost.dbcenterapi.data.api_model.Domain

data class DomainBookingRequest(
    val DomainOrderType: Int,
    val DomainRegService: Int,
    val clientId: String,
    val domainChannelType: Int,
    val domainName: String,
    val domainType: String,
    val existingFPTag: String,
    val validityInYears: String
)