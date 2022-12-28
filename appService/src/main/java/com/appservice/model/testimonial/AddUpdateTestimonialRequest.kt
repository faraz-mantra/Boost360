package com.appservice.model.testimonial

import com.framework.base.BaseRequest
import java.io.Serializable

data class AddUpdateTestimonialRequest(
  var floatingPointId: String? = null,
  var floatingPointTag: String? = null,
  var profileImage: ProfileImage? = null,
  var reviewerCity: String? = null,
  var reviewerName: String? = null,
  var reviewerTitle: String? = null,
  var testimonialBody: String? = null,
  var testimonialDate: String? = null,
  var testimonialTitle: String? = null,
  var testimonialId: String? = null
) : BaseRequest(), Serializable