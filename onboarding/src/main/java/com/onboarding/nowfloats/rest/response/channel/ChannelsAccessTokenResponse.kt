package com.onboarding.nowfloats.rest.response.channel

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.channel.respose.NFXAccessToken
import java.io.Serializable

data class ChannelsAccessTokenResponse(
    val NFXAccessTokens: List<NFXAccessToken>? = null,
    val nowfloats_id: String? = null,
    val callLogTimeInterval: String? = null,
    val smsRegex: List<String>? = null
) : BaseResponse(), Serializable