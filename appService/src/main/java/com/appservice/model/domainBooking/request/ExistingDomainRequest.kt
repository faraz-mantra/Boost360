package com.appservice.model.domainBooking.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExistingDomainRequest(

    @field:SerializedName("ClientId")
    private var clientId: String? = null,

    @field:SerializedName("FPTag")
    private val fPTag: String? = null,

    @field:SerializedName("Subject")
    private val subject: String? = null,

    @field:SerializedName("Mesg")
    private val mesg: String? = null

) : Serializable