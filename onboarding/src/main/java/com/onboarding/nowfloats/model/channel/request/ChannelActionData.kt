package com.onboarding.nowfloats.model.channel.request

import com.google.gson.annotations.SerializedName

data class ChannelActionData(
        @SerializedName("Type")
        var active_whatsapp_number: String? = null
)