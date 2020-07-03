package com.onboarding.nowfloats.model.channel

import java.io.Serializable

data class ChannelTokenResponse(
    val access_token: String? = null,
    val token_type: String? = null,
    val expires_in: Long? = null,
    val refresh_token: String? = null
) : Serializable