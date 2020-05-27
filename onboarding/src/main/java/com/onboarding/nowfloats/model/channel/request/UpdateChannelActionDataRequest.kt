package com.onboarding.nowfloats.model.channel.request

import com.framework.base.BaseRequest

data class UpdateChannelActionDataRequest(
    var ActionData: ChannelActionData? = ChannelActionData(),
    var WebsiteId: String? = null
) : BaseRequest()