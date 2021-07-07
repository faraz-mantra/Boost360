package com.appservice.model.serviceTiming

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddServiceTimingRequest(
  @SerializedName("ServiceId")
  var serviceId: String? = null,
  @SerializedName("Duration")
  var duration: Int? = null,
  @SerializedName("Timings")
  var timings: ArrayList<ServiceTiming>? = null,
) : BaseRequest(), Serializable {

}