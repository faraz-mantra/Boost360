package com.onboarding.nowfloats.model.channel.request

import com.framework.base.BaseRequest

data class UpdateChannelAccessTokenRequest(
        var accessToken: ChannelAccessToken,
        var clientId: String,
        var floatingPointId: String
) : BaseRequest()