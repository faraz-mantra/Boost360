package com.onboarding.nowfloats.model.channel.request

import com.google.gson.annotations.SerializedName

data class ChannelAccessToken(
        val type: AccessTokenType,
        @SerializedName("Type")
        private var _type: String? = type?.name?.toLowerCase(),
        @SerializedName("UserAccessTokenKey")
        var userAccessTokenKey: String? = null,
        @SerializedName("UserAccessTokenSecret")
        var userAccessTokenSecret: String? = null, // TODO Not sure what to send here
        @SerializedName("UserAccountId")
        var userAccountId: String? = null,
        @SerializedName("UserAccountName")
        var userAccountName: String? = null
) {
  enum class AccessTokenType {
    Facebookpage, Facebookshop, GoogleMyBusiness, Twitter
  }

}