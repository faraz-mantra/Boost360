package com.onboarding.nowfloats.model.channel.statusResponse


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChannelAccessStatusResponse(
    @SerializedName("channels")
    var channels: ChannelsType? = null,
    @SerializedName("connected_at")
    var connectedAt: String? = null,
    @SerializedName("last_activity_at")
    var lastActivityAt: String? = null,
    @SerializedName("nowfloats_id")
    var nowfloatsId: String? = null,
    @SerializedName("success")
    var success: Boolean? = null,
) : BaseResponse(), Serializable