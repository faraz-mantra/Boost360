package com.onboarding.nowfloats.model.channel.statusResponse

import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val CHANNEL_STATUS_SUCCESS = "CONNECTED"

data class ChannelsType(
    @SerializedName("facebookpage")
    var facebookpage: Facebookpage? = null,
    @SerializedName("facebookshop")
    var facebookshop: Facebookshop? = null,
    @SerializedName("googlemybusiness")
    var googlemybusiness: Googlemybusiness? = null,
    @SerializedName("twitter")
    var twitter: Twitter? = null,
) : Serializable