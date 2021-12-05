package com.boost.marketplace.infra.api.models.live.addOns

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ManageAddOnsBusinessResponse(
  @SerializedName("data")
  var `data`: ArrayList<BusinessActionData>? = null,
) : BaseResponse(), Serializable