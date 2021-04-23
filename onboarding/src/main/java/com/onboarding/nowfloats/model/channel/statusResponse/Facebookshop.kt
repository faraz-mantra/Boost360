package com.onboarding.nowfloats.model.channel.statusResponse

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Facebookshop(
    @SerializedName("account")
    var account: AccountShop? = null,
    @SerializedName("account_status")
    var accountStatus: String? = null,
    @SerializedName("connected_at")
    var connectedAt: String? = null,
    @SerializedName("errors")
    var errors: List<Error>? = null,
    @SerializedName("last_activity")
    var lastActivity: String? = null,
    @SerializedName("last_post_status")
    var lastPostStatus: LastPostStatus? = null,
    @SerializedName("status")
    var status: String? = null,
) : Serializable