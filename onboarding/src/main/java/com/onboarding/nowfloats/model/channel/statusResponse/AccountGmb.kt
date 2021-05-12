package com.onboarding.nowfloats.model.channel.statusResponse


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountGmb(
    @SerializedName("accountId")
    var accountId: String? = null,
    @SerializedName("accountName")
    var accountName: String? = null,
    @SerializedName("locationId")
    var locationId: String? = null,
    @SerializedName("locationName")
    var locationName: String? = null,
) : Serializable