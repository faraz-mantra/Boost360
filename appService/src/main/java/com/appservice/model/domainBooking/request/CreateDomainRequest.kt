package com.appservice.model.domainBooking.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CreateDomainRequest(
    @field:SerializedName("clientId")
    private val clientId: String? = null,

    @field:SerializedName("domainName")
    private val domainName: String? = null,

    @field:SerializedName("domainType")
    private val domainType: String? = null,

    @field:SerializedName("existingFPTag")
    private val existingFPTag: String? = null,

    @field:SerializedName("domainChannelType")
    private val domainChannelType: Int? = null,

    @field:SerializedName("DomainRegService")
    private val DomainRegService: Int? = null,

    @field:SerializedName("validityInYears")
    private val validityInYears: String? = null,

    @field:SerializedName("DomainOrderType")
    private val DomainOrderType: Int? = null,
) : Serializable