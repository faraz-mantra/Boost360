package com.onboarding.nowfloats.model.channel.respose

import java.io.Serializable

data class WhatsappData(
    val ActionId: String? = null,
    val CreatedOn: String? = null,
    val IsArchived: Boolean? = null,
    val UpdatedOn: String? = null,
    val UserId: String? = null,
    val WebsiteId: String? = null,
    val _id: String? = null,
    val active_whatsapp_number: String? = null
) : Serializable