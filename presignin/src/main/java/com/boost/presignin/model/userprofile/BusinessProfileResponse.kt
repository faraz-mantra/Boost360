package com.boost.presignin.model.userprofile

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BusinessProfileResponse(

    @field:SerializedName("StatusCode")
    var statusCode: Int? = null,

    @field:SerializedName("Result")
    var result: Result? = null,
) : BaseResponse(), Serializable

data class Result(

    @field:SerializedName("FpIds")
    var fpIds: List<String>? = null,

    @field:SerializedName("ParentId")
    var parentId: String? = null,

    @field:SerializedName("ProfileAccessType")
    var profileAccessType: Int? = null,

    @field:SerializedName("LoginId")
    var loginId: String? = null,

    @field:SerializedName("IsEnterprise")
    var isEnterprise: Boolean? = null,

    @field:SerializedName("SourceClientId")
    var sourceClientId: String? = null,

    @field:SerializedName("ProfileProperties")
    var profileProperties: ProfileProperties? = null,

    @field:SerializedName("_id")
    var id: String? = null,

    @field:SerializedName("LoginKey")
    var loginKey: String? = null,

    @field:SerializedName("Provider")
    var provider: String? = null,
):Serializable
