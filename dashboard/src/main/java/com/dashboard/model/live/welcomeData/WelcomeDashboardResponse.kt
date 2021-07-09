package com.dashboard.model.live.welcomeData

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WelcomeDashboardResponse(
    @SerializedName("data")
    var `data`: ArrayList<WelcomeActionData>? = null,
) : BaseResponse(), Serializable