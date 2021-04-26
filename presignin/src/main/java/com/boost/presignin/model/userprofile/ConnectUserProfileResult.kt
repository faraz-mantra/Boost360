package com.boost.presignin.model.userprofile

import com.google.gson.annotations.SerializedName

data class ConnectUserProfileResult(
        @SerializedName("FpIds")
        val FpIds: Array<String>,
        @SerializedName("Channels")
        val Channels: AuthChannels,

        )