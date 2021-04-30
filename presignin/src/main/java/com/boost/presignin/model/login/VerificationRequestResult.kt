package com.boost.presignin.model.login

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VerificationRequestResult(
    @SerializedName("accessType")
    var accessType: Int? = null,
    @SerializedName("applicationId")
    var applicationId: Any? = null,
    @SerializedName("channelProfileProperties")
    var channelProfileProperties: ChannelProfileProperties? = null,
    @SerializedName("isEnterprise")
    var isEnterprise: Boolean? = null,
    @SerializedName("isRestricted")
    var isRestricted: Boolean? = null,
    @SerializedName("loginId")
    var loginId: String? = null,
    @SerializedName("profileImage")
    var profileImage: Any? = null,
    @SerializedName("profileProperties")
    var profileProperties: ProfileProperties? = null,
    @SerializedName("socialShareTokens")
    var socialShareTokens: Any? = null,
    @SerializedName("sourceClientId")
    var sourceClientId: String? = null,
    @SerializedName("ValidFPIds")
    var validFPIds: List<String>? = null
) : BaseResponse(), Serializable