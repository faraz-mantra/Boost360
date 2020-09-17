package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName

data class Result(
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
    @SerializedName("_id")
    val _id: String,
    @SerializedName("ParentId")
    val parentId: String

)