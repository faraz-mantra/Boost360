package com.onboarding.nowfloats.model.channel.request

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName

data class UpdateChannelActionDataRequest(
        @SerializedName("ActionData")
        var actionData: ChannelActionData = ChannelActionData(),
        @SerializedName("WebsiteId")
        var subdomain: String? = null
): BaseRequest()