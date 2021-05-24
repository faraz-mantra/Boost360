package com.boost.presignin.model.userprofile

import com.google.gson.annotations.SerializedName

data class UserResult(
        @SerializedName("AuthToken")
        val AuthToken: String,
        @SerializedName("ClientId")
        val ClientId: String,
        @SerializedName("LoginKey")
        val LoginKey: String,
        @SerializedName("LoginSecret")
        val LoginSecret: String,
        @SerializedName("ProfileProperties")
        val ProfileProperties: ProfileProperties,
        @SerializedName("Provider")
        val Provider: String,
        @SerializedName("FpIds")
        val FpIds: Array<String>,
        @SerializedName("IsEnterprise")
        val IsEnterprise: Boolean,
        @SerializedName("SourceClientId")
        val SourceClientId: String,
        @SerializedName("ProfileAccessType")
        val ProfileAccessType: Int,
        @SerializedName("LoginId")
        val LoginId: String,

        )