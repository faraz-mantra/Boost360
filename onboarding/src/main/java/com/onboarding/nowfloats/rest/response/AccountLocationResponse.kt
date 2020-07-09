package com.onboarding.nowfloats.rest.response

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.model.googleAuth.location.LocationNew
import java.io.Serializable

data class AccountLocationResponse(
    val locations: List<LocationNew>? = null
) : BaseResponse(), Serializable