package com.appservice.model.testimonial.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TestimonialResult(
  @SerializedName("Data")
  var `data`: List<TestimonialData>? = null,
  @SerializedName("Paging")
  var paging: Paging? = null
):Serializable