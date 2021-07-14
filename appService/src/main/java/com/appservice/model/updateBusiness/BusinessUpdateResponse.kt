package com.appservice.model.updateBusiness

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BusinessUpdateResponse(
  @SerializedName("floats")
  var floats: ArrayList<UpdateFloat>? = null,
  @SerializedName("moreFloatsAvailable")
  var moreFloatsAvailable: Boolean? = null,
  @SerializedName("totalCount")
  var totalCount: Int? = null
) : BaseResponse(), Serializable