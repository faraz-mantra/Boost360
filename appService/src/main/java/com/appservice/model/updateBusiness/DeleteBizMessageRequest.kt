package com.appservice.model.updateBusiness


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeleteBizMessageRequest(
  @SerializedName("clientId")
  var clientId: String? = null,
  @SerializedName("dealId")
  var dealId: String? = null,
  @SerializedName("identifierType")
  var identifierType: String? = null
) : BaseRequest(), Serializable