package com.onboarding.nowfloats.model.profile

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MerchantProfileResponse(
    @SerializedName("Error")
    var errorN: ProfileError? = null,
    @SerializedName("Result")
    var result: MerchantResult? = null,
    @SerializedName("StatusCode")
    var statusCode: Int? = null,
) : BaseResponse(), Serializable