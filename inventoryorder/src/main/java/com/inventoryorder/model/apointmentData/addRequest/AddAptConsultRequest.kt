package com.inventoryorder.model.apointmentData.addRequest


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddAptConsultRequest(
  @SerializedName("ActionData")
  var actionData: ActionData? = null,
  @SerializedName("WebsiteId")
  var websiteId: String? = null
) : BaseRequest(), Serializable