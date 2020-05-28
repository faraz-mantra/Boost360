package com.onboarding.nowfloats.rest.response.channel

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.channel.ChannelModel

data class ChannelListResponse(
    var data: ArrayList<ChannelModel> = ArrayList()
) : BaseResponse()
