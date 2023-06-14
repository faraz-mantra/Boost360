package com.boost.presignin.model.onBoardingInfo

import com.framework.base.BaseResponse
import java.io.Serializable

data class OnBoardingInfo(
    val MobileNumber: String,
    val data: Data,
    val emailId: String,
    val clientId: String
) : BaseResponse(), Serializable