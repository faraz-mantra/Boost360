package com.onboarding.nowfloats.model.profile


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChannelProfileProperty(
    @SerializedName("profileProperties")
    var profileProperties: ProfileProperties? = null,
    @SerializedName("Provider")
    var provider: String? = null,
) : Serializable