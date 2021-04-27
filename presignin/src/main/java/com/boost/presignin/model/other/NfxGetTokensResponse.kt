package com.boost.presignin.model.other

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*


data class NfxGetTokensResponse(
        @SerializedName("message")
        var message: String? = null,
        @SerializedName("nowfloats_id")
        var nowfloatsId: String? = null,
        @SerializedName("callLogTimeInterval")
        var callLogTimeInterval: String? = null,
        @SerializedName("smsRegex")
        var smsRegex: List<String>? = null,
        @SerializedName("NFXAccessTokens")
        var nFXAccessTokens: List<NFXAccessToken> = ArrayList(),

        ) {

    data class NFXAccessToken(
            @SerializedName("Type")
            @Expose
            var type: String? = null,
            @SerializedName("Status")
            @Expose
            var status: String? = null,
            @SerializedName("UserAccessTokenKey")
            @Expose
            var userAccessTokenKey: String? = null,
            @SerializedName("UserAccessTokenSecret")
            @Expose
            var userAccessTokenSecret: String? = null,
            @SerializedName("UserAccountId")
            @Expose
            var userAccountId: String? = null,
            @SerializedName("UserAccountName")
            @Expose
            var userAccountName: String? = null,
    ) {

    }
}
