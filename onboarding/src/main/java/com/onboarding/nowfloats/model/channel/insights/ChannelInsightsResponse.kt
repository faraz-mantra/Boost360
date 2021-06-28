package com.onboarding.nowfloats.model.channel.insights

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChannelInsightsResponse(
    @SerializedName("channel")
    var channel: String? = null,
    @SerializedName("data")
    var `data`: InsightsData? = null,
    @SerializedName("nowfloats_id")
    var nowfloatsId: String? = null,
    @SerializedName("success")
    var success: Boolean? = null,
) : BaseResponse(), Serializable