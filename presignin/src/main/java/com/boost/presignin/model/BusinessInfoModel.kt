package com.boost.presignin.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BusinessInfoModel(
        @SerializedName("userEmail")
        var userEmail: String? = null,
        @SerializedName("userMobile")
        var userMobile: String? = null,
        @SerializedName("userName")
        var userName: String? = null,
        @SerializedName("userPassword")
        val userPassword: String? = null,
        @SerializedName("businessName")
        var businessName: String? = null,
        @SerializedName("domainName")
        var domainName: String? = null

) : BaseResponse(), Serializable
