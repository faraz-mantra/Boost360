package com.boost.dbcenterapi.data.api_model.Domain.AlreadyPurchasedDomainResponse

data class PurchasedDomainResponse(
    val ActivatedOn: String,
    val ErrorMessage: Any,
    val ExpiresOn: String,
    val NameServers: Any,
    val domainName: String,
    val domainType: String,
    val fpTag: String,
    val hasDomain: Boolean,
    val isActive: Boolean,
    val isExpired: Boolean,
    val isFailed: Boolean,
    val isLinked: Boolean,
    val isPending: Boolean
)