package com.framework.models.firestore.restApi.model


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdateDrRequest(
  @SerializedName("client_id")
  var clientId: String? = null,
  @SerializedName("fp_tag")
  var fpTag: String? = null,
  @SerializedName("key")
  var key: String? = null,
  @SerializedName("value")
  var value: Any? = null
) : BaseRequest(), Serializable