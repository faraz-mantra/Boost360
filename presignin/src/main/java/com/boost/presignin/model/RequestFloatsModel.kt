package com.boost.presignin.model

import android.os.Parcel
import android.os.Parcelable
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.rest.userprofile.ProfileProperties
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RequestFloatsModel(
        @SerializedName("AuthToken")
        var AuthToken: String?=null,
        @SerializedName("ClientId")
        var ClientId: String?=null,
        @SerializedName("LoginKey")
        var LoginKey: String?=null,
        @SerializedName("LoginSecret")
        var LoginSecret: String?=null,
        @SerializedName("ProfileProperties")
        var ProfileProperties: BusinessInfoModel,
        @SerializedName("Provider")
        var Provider: String?=null,
        var categoryDataModel: CategoryDataModel? = null,
        var businessUrl: String? = null,
        var webSiteUrl: String? = null,
        var whatsAppFlag: Boolean? = null,
        var channelAccessTokens: ArrayList<ChannelAccessToken>? = ArrayList(),
        var channelActionDatas: ArrayList<ChannelActionData>? = ArrayList(),
) :BaseResponse(), Serializable {
}

data class ChannelAccessToken(
        @SerializedName("Type")
        var type: String? = null,
        @SerializedName("UserAccessTokenKey")
        var userAccessTokenKey: String? = null,
        @SerializedName("UserAccessTokenSecret")
        var userAccessTokenSecret: String? = null,
        @SerializedName("UserAccountId")
        var userAccountId: String? = null,
        @SerializedName("UserAccountName")
        var userAccountName: String? = null,

        //TODO  for shop
        @SerializedName("PixelId")
        var pixelId: String? = null,
        @SerializedName("CatalogId")
        var catalogId: String? = null,
        @SerializedName("MerchantSettingsId")
        var merchantSettingsId: String? = null,

        //TODO google business
        var token_expiry: String? = null,
        var invalid: Boolean? = null,
        var token_response: ChannelTokenResponse? = null,
        var refresh_token: String? = null,
//    var account_name: String? = null,
//    var account_id: String? = null,
//    var location_id: String? = null,
        var LocationId: String? = null,
//    var location_name: String? = null,
        var LocationName: String? = null,
        var verified_location: Boolean? = null,

        //TODO url
        var profilePicture: String? = null

)

data class ChannelTokenResponse(
        val access_token: String? = null,
        val token_type: String? = null,
        val expires_in: Long? = null,
        val refresh_token: String? = null
) : Serializable
data class ChannelActionData(
        var active_whatsapp_number: String? = null
)