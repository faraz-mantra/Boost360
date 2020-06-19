package com.onboarding.nowfloats.rest.response.channel

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.channel.respose.Extra
import com.onboarding.nowfloats.model.channel.respose.WhatsappData
import java.io.Serializable

data class ChannelWhatsappResponse(
    val Data: List<WhatsappData>? = null,
    val Extra: Extra? = null
) : BaseResponse(), Serializable