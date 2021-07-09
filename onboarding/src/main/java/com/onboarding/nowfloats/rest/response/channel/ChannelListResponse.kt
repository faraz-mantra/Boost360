package com.onboarding.nowfloats.rest.response.channel

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.channel.ChannelModel
import java.io.Serializable

data class ChannelListResponse(
    var data: ArrayList<ChannelModel> = ArrayList()
) : BaseResponse(), Serializable
