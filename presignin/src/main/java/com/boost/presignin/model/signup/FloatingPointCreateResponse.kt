package com.boost.presignin.model.signup


import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.login.ChannelProfileProperties
import com.boost.presignin.model.login.ProfileProperties
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FloatingPointCreateResponse(
    @SerializedName("accessType")
    var accessType: Int? = null,
    @SerializedName("applicationId")
    var applicationId: Any? = null,
    @SerializedName("authProviderId")
    var authProviderId: Any? = null,
    @SerializedName("authTokens")
    var authTokens: ArrayList<AuthTokenDataItem>? = null,
    @SerializedName("channelProfileProperties")
    var channelProfileProperties: ChannelProfileProperties? = null,
    @SerializedName("extraInformation")
    var extraInformation: ExtraInformation? = null,
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
    var validFPIds: ArrayList<String>? = null,
) : BaseResponse(), Serializable