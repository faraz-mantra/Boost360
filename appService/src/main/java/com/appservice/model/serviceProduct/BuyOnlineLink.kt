package com.appservice.model.serviceProduct

import java.io.Serializable

data class BuyOnlineLink(
    var url: String? = null,
    var description: String? = null
) : Serializable