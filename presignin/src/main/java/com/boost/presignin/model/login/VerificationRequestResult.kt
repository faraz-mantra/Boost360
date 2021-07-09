package com.boost.presignin.model.login

import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VerificationRequestResult(
    @SerializedName("accessType")
    var accessType: Int? = null,
    @SerializedName("applicationId")
    var applicationId: String? = null,
    @SerializedName("channelProfileProperties")
    var channelProfileProperties: ChannelProfileProperties? = null,
    @SerializedName("isEnterprise")
    var isEnterprise: Boolean? = null,
    @SerializedName("isRestricted")
    var isRestricted: Boolean? = null,
    @SerializedName("loginId")
    var loginId: String? = null,
    @SerializedName("profileImage")
    var profileImage: String? = null,
    @SerializedName("profileProperties")
    var profileProperties: ProfileProperties? = null,
    @SerializedName("socialShareTokens")
    var socialShareTokens: Any? = null,
    @SerializedName("sourceClientId")
    var sourceClientId: String? = null,
    @SerializedName("ValidFPIds")
    var validFPIds: ArrayList<String>? = null,
    @SerializedName("authTokens")
    var authTokens: ArrayList<AuthTokenDataItem>? = null,
) : BaseResponse(), Serializable