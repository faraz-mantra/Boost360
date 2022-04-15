package com.appservice.model.testimonial

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TestimonialAddResponse(
  @SerializedName("Error")
  var error1: Any? = null,
  @SerializedName("Result")
  var result: String? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null
) : BaseResponse(), Serializable