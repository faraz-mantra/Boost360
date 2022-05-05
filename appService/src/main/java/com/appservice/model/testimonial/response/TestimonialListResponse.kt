package com.appservice.model.testimonial.response

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TestimonialListResponse(
  @SerializedName("Error")
  var error1: Any? = null,
  @SerializedName("Result")
  var result: TestimonialResult? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null
) : BaseResponse(), Serializable