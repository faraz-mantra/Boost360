package com.onboarding.nowfloats.model.channel.request

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName

data class UpdateChannelActionDataRequest(
    var ActionData: ChannelActionData = ChannelActionData(),
    var WebsiteId: String? = null
) : BaseRequest()