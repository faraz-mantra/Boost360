package com.appservice.model.testimonial

import com.framework.base.BaseRequest
import java.io.Serializable

data class ListTestimonialRequest(
  var floatingPointId: String? = null,
  var floatingPointTag: String? = null,
  var limit: Int? = null,
  var offset: Int? = null
) : BaseRequest(), Serializable